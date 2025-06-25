package com.nextroom.app.entrata.service;

import com.nextroom.app.entrata.dto.AccessTokenResponseDTO;

public interface AccessTokenService {

    public String getValidToken(Long landlordId);
}
