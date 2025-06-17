package com.nextroom.app.web.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nextroom.app.common.exception.EntityNotFoundException;
import com.nextroom.app.common.exception.QRCodeGenerationException;
import com.nextroom.app.common.exception.UnauthorizedException;
import com.nextroom.app.web.dto.InviteActionRequestDTO;
import com.nextroom.app.web.dto.InviteActionResponseDTO;
import com.nextroom.app.web.dto.InviteResponseDTO;
import com.nextroom.app.web.dto.InviteValidationResponseDTO;
import com.nextroom.app.web.model.Invite;
import com.nextroom.app.web.model.InviteStatus;
import com.nextroom.app.web.model.Roommate;
import com.nextroom.app.web.model.User;
import com.nextroom.app.web.repository.InviteRepository;
import com.nextroom.app.web.repository.RoommateRepository;
import com.nextroom.app.web.service.InviteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class InviteServiceImpl implements InviteService {

    private static final Logger logger = LogManager.getLogger(InviteServiceImpl.class);

    private final InviteRepository inviteRepository;
    private final RoommateRepository roommateRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public InviteServiceImpl(InviteRepository inviteRepository, RoommateRepository roommateRepository, ModelMapper modelMapper) {
        this.inviteRepository = inviteRepository;
        this.roommateRepository = roommateRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public InviteResponseDTO createInvite(User inviter) {
        try {
            String slug = generateSlug(inviter);
            String inviteUrl = "https://nextroom.ca/invite/" + slug;
            String qrBase64 = generateQRCodeBase64(inviteUrl);

            Invite invite = new Invite();
            invite.setSlug(slug);
            invite.setUrl(inviteUrl);
            invite.setQrCodePath(qrBase64);
            invite.setInvitedBy(inviter);
            invite.setStatus(InviteStatus.PENDING);
            invite.setCreatedAt(LocalDateTime.now());

            inviteRepository.save(invite);
            logger.info("Invite url created successfully for inviter: {}", inviter.getEmail());

            InviteResponseDTO response = modelMapper.map(invite, InviteResponseDTO.class);
            response.setInviterName(inviter.getFirstName() + " " + inviter.getLastName());

            return response;

        } catch (UnauthorizedException | EntityNotFoundException e) {
            logger.error("Invite creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating invite", e);
            throw new RuntimeException("Failed to create invite", e);
        }
    }

    private String generateSlug(User user) {
        String firstName = user.getFirstName().toLowerCase().replaceAll("[^a-z0-9]", "");
        String lastName = user.getLastName().toLowerCase().replaceAll("[^a-z0-9]", "");
        String university = user.getUniversity();

        String baseSlug = firstName + "-" + lastName.charAt(0); // Rule 1: first-lastInitial
        if (!inviteRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        String fullLastNameSlug = firstName + "-" + lastName; // Rule 2: first-fullLastName
        if (!inviteRepository.existsBySlug(fullLastNameSlug)) {
            return fullLastNameSlug;
        }

        String schoolCode = getSchoolCode(university);
        int suffix = 1;

        String finalSlug;
        do {
            finalSlug = fullLastNameSlug + "-" + schoolCode + suffix;
            suffix++;
        } while (inviteRepository.existsBySlug(finalSlug)); // Rule 3: first-lastname-uo7

        return finalSlug;
    }

    private String getSchoolCode(String university) {
        return switch (university.toLowerCase()) {
            case "the university of ottawa" -> "uo";
            case "carleton university" -> "cu";
            case "algonquin college" -> "ac";
            case "collége la cité" -> "lc";
            default -> "xx";
        };
    }

    private String generateQRCodeBase64(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] pngData = outputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);

        } catch (WriterException | IOException e) {
            logger.error("QR code generation failed for text: {}", text, e);
            throw new QRCodeGenerationException("Failed to generate QR code", e);
        }
    }

    @Override
    public InviteValidationResponseDTO validateInviteSlug(String slug) {
        try {
            Invite invite = inviteRepository.findBySlug(slug)
                    .orElseThrow(() -> new EntityNotFoundException("Invite not found"));

            InviteValidationResponseDTO response = new InviteValidationResponseDTO();
            response.setInviteId(invite.getId());
            response.setSlug(invite.getSlug());
            response.setStatus(invite.getStatus().name());
            User inviter = invite.getInvitedBy();
            response.setInviterName(inviter.getFirstName() + " " + inviter.getLastName());

            return response;
        } catch (EntityNotFoundException e) {
            logger.error("Validation failed, invite not found for slug: {}", slug);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error during invite validation", e);
            throw new RuntimeException("Failed to validate invite");
        }
    }

    @Override
    @Transactional
    public InviteActionResponseDTO handleInviteAction(Long inviteId, InviteActionRequestDTO requestDTO, User invitee) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found with ID: " + inviteId));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalStateException("Invite is already " + invite.getStatus());
        }

        InviteStatus newStatus;
        try {
            newStatus = InviteStatus.valueOf(requestDTO.getAction().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid action: must be ACCEPT or REJECT");
        }

        invite.setStatus(newStatus);
        inviteRepository.save(invite);

        if (newStatus == InviteStatus.ACCEPTED) {
            Roommate roommate = new Roommate()
                    .setInviter(invite.getInvitedBy())
                    .setInvitee(invitee);

            roommateRepository.save(roommate);
        }

        InviteActionResponseDTO response = new InviteActionResponseDTO();
        response.setInviteId(inviteId);
        response.setStatus(newStatus.toString());
        response.setMessage("Invite " + newStatus.name().toLowerCase() + " successfully");

        return response;
    }

}