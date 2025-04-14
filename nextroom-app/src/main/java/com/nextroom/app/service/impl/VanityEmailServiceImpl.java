package com.nextroom.app.service.impl;

import com.nextroom.app.dto.EmailRequestDTO;
import com.nextroom.app.model.User;
import com.nextroom.app.security.JwtService;
import com.nextroom.app.service.EmailService;
import com.nextroom.app.service.UserService;
import com.nextroom.app.service.VanityEmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VanityEmailServiceImpl implements VanityEmailService {

    private static final Logger logger = LogManager.getLogger(VanityEmailServiceImpl.class);

    private final JwtService jwtService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public VanityEmailServiceImpl(JwtService jwtService, UserService userService, EmailService emailService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public String sendVanityEmail(EmailRequestDTO emailRequestDTO, String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }

            String userEmail = jwtService.extractUsername(token);
            logger.info("User email: {}", userEmail);

            User userDetails = userService.findUserByEmail(userEmail);
            if (userDetails == null) {
                return "User not found";
            }

            for (int i = 0; i < emailRequestDTO.getSelectedCompanies().size(); i++) {
                String company = emailRequestDTO.getSelectedCompanies().get(i);
                String property = emailRequestDTO.getSelectedProperties().get(i);

                // Prepare email content
                String subject = String.format("New Student Inquiry from Next Room – %s", property);
                String body = buildEmailBody(userDetails, property);

                sendEmailToCompany(company, subject, body);
            }

            return "Emails sent successfully.";
        } catch (Exception e) {
            return String.format("Error sending email: %s", e.getMessage());
        }
    }

    private String buildEmailBody(User userDetails, String property) {
        StringBuilder body = new StringBuilder();

        body.append(String.format("Name: %s %s<br>", userDetails.getFirstName(), userDetails.getLastName()));
        body.append(String.format("Email: %s<br>", userDetails.getEmail()));

        if (userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().isEmpty()) {
            body.append(String.format("Phone: %s<br>", userDetails.getPhoneNumber()));
        }

        body.append(String.format("University: %s<br>", userDetails.getUniversity()));
        body.append(String.format("Age: %s<br>", userDetails.getAge()));
        body.append("Move-in Date: May 1, 2025<br>");
        body.append("<br>");
        body.append("Referred by: Next Room Inc.<br>");
        body.append(String.format("Listing of interest: %s<br>", property));

        return body.toString();
    }

    private void sendEmailToCompany(String company, String subject, String body) {
        String companyEmail = getCompanyEmail(company);
        if (companyEmail != null) {
            emailService.sendPromotionEmail(companyEmail, subject, body);
        }
    }

    private String getCompanyEmail(String company) {
        return switch (company) {
            case "ALMA" -> "100056185-104285-899457-18981@guestcardlead.com";
            case "THEO" -> "1238513-104285-899458-18981@guestcardlead.com";
            case "1eleven" -> "1238484-104285-899459-18981@guestcardlead.com";
            default -> null;
        };
    }
}
