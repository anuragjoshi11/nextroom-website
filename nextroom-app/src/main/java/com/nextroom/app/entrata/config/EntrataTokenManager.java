package com.nextroom.app.entrata.config;

import com.nextroom.app.entrata.model.EntrataAccessToken;
import com.nextroom.app.entrata.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntrataTokenManager {

    private final AccessTokenService accessTokenService;

    @Autowired
    public EntrataTokenManager(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    /**
     * Retrieves a valid Entrata access token for the given landlord ID.
     * Fetches a new one and stores it if expired or not present.
     *
     * @param landlordId Landlord's unique identifier
     * @return Valid Entrata access token
     */
    public String getAccessToken(Long landlordId) {
        return accessTokenService.getValidToken(landlordId);
    }
}

