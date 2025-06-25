package com.nextroom.app.entrata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class EntrataActivationEventDTO {

    private UUID landlordId;

    @NotBlank
    private String cid;

    private String companyName;

    private String subdomain;

    private String authCode;

    private String apiLocation;

    private String propertyId;

    private String occupancyType;

    private String requestType;

    private String referrer;
}

