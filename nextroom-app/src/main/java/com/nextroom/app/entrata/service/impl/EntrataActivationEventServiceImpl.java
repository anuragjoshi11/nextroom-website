package com.nextroom.app.entrata.service.impl;

import com.nextroom.app.entrata.dto.EntrataActivationEventDTO;
import com.nextroom.app.entrata.model.EntrataActivationEvent;
import com.nextroom.app.entrata.repository.EntrataActivationEventRepository;
import com.nextroom.app.entrata.service.EntrataActivationEventService;
import com.nextroom.app.web.model.Landlord;
import com.nextroom.app.web.repository.LandlordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EntrataActivationEventServiceImpl implements EntrataActivationEventService {

    private static final Logger logger = LogManager.getLogger(EntrataActivationEventServiceImpl.class);

    private final LandlordRepository landlordRepository;
    private final EntrataActivationEventRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public EntrataActivationEventServiceImpl(LandlordRepository landlordRepository, EntrataActivationEventRepository repository, ModelMapper modelMapper) {
        this.landlordRepository = landlordRepository;
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String saveEvent(EntrataActivationEventDTO dto) {
        try {
            logger.info("Processing Entrata webhook DTO: {}", dto);

            Optional<Landlord> landlordOpt = landlordRepository.findByCid(dto.getCid());

            // Set landlord ID in DTO and Event if found
            if (landlordOpt.isPresent()) {
                Landlord landlord = landlordOpt.get();
                dto.setLandlordId(landlord.getLandlordId());
                logger.info("Landlord matched: cid={}, landlordId={}", dto.getCid(), landlord.getLandlordId());

                if (!landlord.isVerified()) {
                    landlord.setVerified(true);
                    landlordRepository.save(landlord);
                    logger.info("Landlord marked as verified: landlordId={}", landlord.getLandlordId());
                }
            } else {
                logger.warn("No landlord found for cid: {}. Saving event without verification.", dto.getCid());
            }

            EntrataActivationEvent event = modelMapper.map(dto, EntrataActivationEvent.class);

            // Check for duplicate event before saving
            if (dto.getPropertyId() != null && repository.existsByCidAndPropertyId(dto.getCid(), dto.getPropertyId())) {
                logger.info("Duplicate event detected for cid={} and propertyId={}. Skipping save.", dto.getCid(), dto.getPropertyId());
                return "Duplicate event. This webhook has already been processed.";
            }

            // Save with DB-level fallback in case of race condition
            try {
                event.setProcessed(true);
                repository.save(event);
                logger.info("Entrata activation event saved successfully for cid={}, propertyId={}", dto.getCid(), dto.getPropertyId());
            } catch (DataIntegrityViolationException e) {
                if (e.getMessage().contains("entrata_activation_events_cid_property_id_key")) {
                    logger.warn("Duplicate insert blocked by DB: cid={}, propertyId={}", dto.getCid(), dto.getPropertyId());
                    return "Duplicate webhook. Event was already saved.";
                }
                throw e;
            }

            return "Webhook received and processed successfully.";

        } catch (DataAccessException e) {
            logger.error("Database error while saving Entrata activation event: {}", e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error while processing Entrata webhook: {}", e.getMessage(), e);
            throw new IllegalStateException("Unexpected error during webhook processing", e);
        }
    }

    @Override
    public Optional<EntrataActivationEvent> getByLandlordId(Long landlordId) {
        return repository.findByLandlordId(landlordId);
    }

    @Override
    public boolean existsByLandlordId(Long landlordId) {
        return repository.existsByLandlordId(landlordId);
    }
}

