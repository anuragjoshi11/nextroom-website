package com.nextroom.app.service;

public interface EmailService {
    public void sendSignupEmail(String email);
    public void sendPromotionEmail(String to, String subject, String body);
}
