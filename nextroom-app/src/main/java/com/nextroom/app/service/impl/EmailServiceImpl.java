package com.nextroom.app.service.impl;

import com.nextroom.app.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.nextroom.app.constants.Constants.BREVO_API_URL;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.vanity.sender.email}")
    private String vanitySenderEmail;

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
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Email sent successfully!");
        } else {
            System.out.println("Error sending email: " + response.getBody());
        }
    }

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
        emailData.put("htmlContent", body); // The body here is HTML content passed from the front-end

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);  // Brevo API Key
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare HTTP entity for the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailData, headers);

        // Create RestTemplate and send the request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL, HttpMethod.POST, entity, String.class);
        // Handle the response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Promotion email sent successfully!");
        } else {
            System.out.println("Error sending promotion email: " + response.getBody());
        }
    }

}
