package com.nextroom.app.entrata.service.impl;

import com.nextroom.app.common.exception.AccessTokenFetchException;
import com.nextroom.app.entrata.dto.AccessTokenRequestDTO;
import com.nextroom.app.entrata.dto.AccessTokenResponseDTO;
import com.nextroom.app.entrata.model.EntrataAccessToken;
import com.nextroom.app.entrata.repository.EntrataAccessTokenRepository;
import com.nextroom.app.entrata.service.AccessTokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final Logger logger = LogManager.getLogger(AccessTokenServiceImpl.class);

    @Value("${entrata.client.id}")
    private String clientId;

    @Value("${entrata.client.secret}")
    private String clientSecret;

    @Value("${entrata.auth.code}")
    private String authCode;

    private final RestClient restClient;
    private final EntrataAccessTokenRepository tokenRepository;

    @Autowired
    public AccessTokenServiceImpl(RestClient restClient, EntrataAccessTokenRepository tokenRepository) {
        this.restClient = restClient;
        this.tokenRepository = tokenRepository;
    }

    public String getValidToken(Long landlordId) {
        Optional<EntrataAccessToken> existingTokenOpt = tokenRepository.findByLandlordId(landlordId);

        if (existingTokenOpt.isPresent()) {
            EntrataAccessToken token = existingTokenOpt.get();
            if (token.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(5))) {
                logger.info("Using cached Entrata token for landlordId={}", landlordId);
                return token.getAccessToken();
            }
        }

        logger.info("Fetching new Entrata token for landlordId={}", landlordId);

        AccessTokenResponseDTO response = fetchAccessTokenFromEntrata();

        AccessTokenResponseDTO.Response innerResponse = response.getResponse();

        if (innerResponse.getError() != null) {
            logger.error("Entrata API returned error: code={}, message={}",
                    innerResponse.getError().getCode(),
                    innerResponse.getError().getMessage());
            throw new AccessTokenFetchException("Entrata error: " + innerResponse.getError().getMessage());
        }

        if (innerResponse.getResult() == null || innerResponse.getResult().getAccess_token() == null) {
            throw new AccessTokenFetchException("Entrata token response structure is invalid");
        }

        String newToken = innerResponse.getResult().getAccess_token();
        long expiresIn = Long.parseLong(innerResponse.getResult().getExpires_in());

        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(expiresIn - 60);

        EntrataAccessToken tokenToSave = existingTokenOpt
                .map(token -> {
                    token.setAccessToken(newToken);
                    token.setExpiresAt(expiryTime);
                    return token;
                })
                .orElseGet(() -> EntrataAccessToken.builder()
                        .landlordId(landlordId)
                        .accessToken(newToken)
                        .expiresAt(expiryTime)
                        .build());

        tokenRepository.save(tokenToSave);
        logger.info("Saved new Entrata token for landlordId={}", landlordId);

        return newToken;
    }

    private AccessTokenResponseDTO fetchAccessTokenFromEntrata() {
        try {
            AccessTokenRequestDTO request = new AccessTokenRequestDTO();

            AccessTokenRequestDTO.Auth auth = new AccessTokenRequestDTO.Auth();
            auth.setType("oauth");
            auth.setCode(authCode);
            auth.setGrant_type("authorization_code");
            auth.setClient_id(clientId);
            auth.setClient_secret(clientSecret);

            AccessTokenRequestDTO.Method method = new AccessTokenRequestDTO.Method();
            method.setName("getAccessToken");
            method.setParams(Collections.emptyList());

            request.setAuth(auth);
            request.setMethod(method);

            logger.info("Sending request to Entrata token endpoint...");

            AccessTokenResponseDTO response = restClient.post()
                    .uri("https://mounts1.entrata.com/api/oauth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AccessTokenResponseDTO.class);

            logger.info("Entrata response received.");
            return response;

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("HTTP error from Entrata API: {}", ex.getStatusCode(), ex);
            throw new AccessTokenFetchException("Entrata API responded with HTTP error: " + ex.getStatusCode());
        } catch (RestClientException ex) {
            logger.error("RestClientException while calling Entrata API", ex);
            throw new AccessTokenFetchException("Failed to call Entrata API");
        } catch (NullPointerException ex) {
            logger.error("NullPointerException in Entrata API response", ex);
            throw new AccessTokenFetchException("Unexpected null in response body");
        }
    }
}
