package com.nextroom.app.service;

import com.nextroom.app.dto.EmailRequestDTO;

public interface VanityEmailService {

    public String sendVanityEmail(EmailRequestDTO emailRequestDTO, String token);
}
