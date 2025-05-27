package com.nextroom.app.service.impl;

import com.nextroom.app.service.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.nextroom.app.constants.Constants.BREVO_API_URL;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.vanity.sender.email}")
    private String vanitySenderEmail;

    @Value("${brevo.password.rest.sender.email}")
    private String pwdResetSenderEmail;

    @Override
    public void sendSignupEmail(String recipientEmail) {
        Map<String, Object> emailData = new HashMap<>();

        // Sender information
        Map<String, String> sender = new HashMap<>();
        sender.put("email", senderEmail);
        emailData.put("sender", sender);

        // Recipient information
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", recipientEmail);
        emailData.put("to", new Object[]{recipient});

        // Email subject and body (insert your HTML content here)
        emailData.put("subject", "Welcome to Next Room!");
        emailData.put("htmlContent", "<html><body>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' style='background-color: #ffffff;'>" +
                "<tr><td>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' align='center' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td valign='top' class='r1-i' style='background-color: transparent;'>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='600' align='center' class='r0-o' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td class='r3-i' style='padding-bottom: 5px; padding-top: 5px;'>" +
                "<table width='100%' cellspacing='0' cellpadding='0' border='0' role='presentation'>" +
                "<tr><th width='100%' valign='top' class='r4-c' style='font-weight: normal;'>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' align='center' class='r0-o' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td align='center' class='r6-i nl2go-default-textstyle' style='color: #414141; font-family: Poppins, Arial; font-size: 16px; line-height: 1.5; word-break: break-word; padding-left: 30px; padding-right: 30px; text-align: center;'>" +
                "Welcome to Next Room!" +
                "</td></tr></table></th></tr></table></td></tr></table></td></tr></table></td></tr></table>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='600' align='center' class='r0-o' style='table-layout: fixed; width: 600px;'>" +
                "<tr><td valign='top' class='r7-i' style='background-color: #ffffff;'>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' align='center' class='r0-o' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td class='r8-i' style='padding-bottom: 80px; padding-top: 20px;'>" +
                "<table width='100%' cellspacing='0' cellpadding='0' border='0' role='presentation'>" +
                "<tr><th width='100%' valign='top' class='r4-c' style='font-weight: normal;'>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' class='r5-o' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td valign='top' class='r9-i' style='padding-left: 15px; padding-right: 15px;'>" +
                "<table width='100%' cellspacing='0' cellpadding='0' border='0' role='presentation'>" +
                "<tr><td class='r2-c' align='center'>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='200' class='r0-o' style='table-layout: fixed; width: 200px;'>" +
                "<tr><td style='font-size: 0px; line-height: 0px;'>" +
                "<img src='https://img.mailinblue.com/8555776/images/content_library/original/67bea149b3248a9ff3219c99.png' width='200' border='0' style='display: block; width: 100%;'></td></tr></table></td></tr></table></td></tr></table></th></tr></table></td></tr></table></td></tr></table>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' align='center' class='r0-o' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td class='r10-i' style='padding-bottom: 20px; padding-top: 20px;'>" +
                "<table width='100%' cellspacing='0' cellpadding='0' border='0' role='presentation'>" +
                "<tr><th width='100%' valign='top' class='r4-c' style='font-weight: normal;'>" +
                "<table cellspacing='0' cellpadding='0' border='0' role='presentation' width='100%' class='r5-o' style='table-layout: fixed; width: 100%;'>" +
                "<tr><td valign='top' class='r9-i' style='padding-left: 15px; padding-right: 15px;'>" +
                "<table width='100%' cellspacing='0' cellpadding='0' border='0' role='presentation'>" +
                "<tr><td class='r11-c' align='center' style='border-radius: 8px; font-size: 0px; line-height: 0px; valign: top;'>" +
                "<img src='https://img.mailinblue.com/8555776/images/content_library/original/67db09eccf3d6ff2d5fee473.jpg' width='570' border='0' style='display: block; width: 100%; border-radius: 8px;'></td></tr></table></td></tr></table></th></tr></table></td></tr></table></td></tr></table>" +
                "<p style='font-family: Arial, sans-serif; font-size: 14px; text-align: left; color: #414141; padding-left: 20px;'>" +
                "Visit our website for more details: <a href='https://nextroom.ca' style='color: #007BFF; text-decoration: none;'>Next Room</a>" +
                "</p>" + "<p style='font-family: Arial, sans-serif; font-size: 14px; text-align: left; color: #414141; padding-left: 20px;'>" +
                "<strong>Next Room</strong><br>90 University Private, K1N 6N5, Ottawa" + "</p>" +
                "</body></html>");
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Make the HTTP request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailData, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL, HttpMethod.POST, entity, String.class);

        // Handle response (log success or failure)
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Email sent successfully!");
        } else {
            logger.error("Error sending email: {}", response.getBody());
        }
    }

    @Override
    public void sendPromotionEmail(String to, String subject, String body) {
        Map<String, Object> emailData = new HashMap<>();

        // Sender information
        Map<String, String> sender = new HashMap<>();
        sender.put("email", vanitySenderEmail);
        emailData.put("sender", sender);

        // Recipient information
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", to);
        emailData.put("to", new Object[]{recipient});

        // Subject and body
        emailData.put("subject", subject);
        emailData.put("htmlContent", body);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare HTTP entity for the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailData, headers);

        // Create RestTemplate and send the request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL, HttpMethod.POST, entity, String.class);
        // Handle the response
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Promotion email sent successfully!");
        } else {
            logger.error("Error sending promotion email: {}", response.getBody());
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        Map<String, Object> emailData = new HashMap<>();

        // Sender info
        Map<String, String> sender = new HashMap<>();
        sender.put("email", pwdResetSenderEmail);
        emailData.put("sender", sender);

        // Recipient info
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", to);
        emailData.put("to", new Object[]{recipient});

        // Email content
        emailData.put("subject", "Reset your password - Next Room");

        String html = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<h2>Password Reset Request</h2>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href='" + resetLink + "' style='background-color: #007BFF; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                "<p>If you didn't request this, you can ignore this email.</p>" +
                "<br><p>Thanks,<br><strong>Next Room Team</strong></p>" +
                "</body></html>";

        emailData.put("htmlContent", html);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailData, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Password reset email sent to {}", to);
        } else {
            logger.error("Failed to send password reset email to {}: {}", to, response.getBody());
        }
    }

    @Override
    public void sendVerificationEmail(String recipientEmail, String verifyLink) {
        Map<String, Object> emailData = new HashMap<>();

        // Sender
        Map<String, String> sender = new HashMap<>();
        sender.put("email", pwdResetSenderEmail);
        emailData.put("sender", sender);

        // Recipient
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", recipientEmail);
        emailData.put("to", new Object[]{recipient});

        // Subject and Email Body
        emailData.put("subject", "Verify Your Email - Next Room");
        emailData.put("htmlContent", "<html><body>" +
                "<h2 style='font-family: Arial, sans-serif; text-align: center;'>Welcome to Next Room!</h2>" +
                "<p style='font-family: Arial, sans-serif; font-size: 14px; color: #414141; padding-left: 20px;'>" +
                "Thank you for signing up. Please click the button below to verify your email address and activate your account." +
                "</p>" +
                "<div style='text-align: center; margin: 30px;'>" +
                "<a href='" + verifyLink + "' style='background-color: #007BFF; color: white; padding: 12px 24px; border-radius: 6px; font-family: Arial, sans-serif; text-decoration: none;'>Verify Account</a>" +
                "</div>" +
                "<p style='font-family: Arial, sans-serif; font-size: 14px; color: #414141; padding-left: 20px;'>" +
                "If you didn’t request this, you can ignore this email." +
                "</p>" +
                "<p style='font-family: Arial, sans-serif; font-size: 14px; color: #414141; padding-left: 20px;'>" +
                "Visit our website: <a href='https://nextroom.ca' style='color: #007BFF;'>Next Room</a><br>" +
                "<strong>Next Room</strong><br>90 University Private, K1N 6N5, Ottawa" +
                "</p></body></html>");

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailData, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(BREVO_API_URL, HttpMethod.POST, entity, String.class);

        // Log result
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Verification email sent successfully to {}", recipientEmail);
        } else {
            logger.error("Failed to send verification email to {}: {}", recipientEmail, response.getBody());
        }
    }

}
