package com.nextroom.app.web.service;

import com.nextroom.app.web.dto.EmailRequestDTO;

public interface VanityEmailService {

    public String sendVanityEmail(EmailRequestDTO emailRequestDTO, String token);
}
