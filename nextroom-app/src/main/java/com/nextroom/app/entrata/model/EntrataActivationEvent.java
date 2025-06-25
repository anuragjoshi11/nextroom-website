package com.nextroom.app.entrata.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "entrata_activation_events", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cid", "property_id"})
})
public class EntrataActivationEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "landlord_id", nullable = false)
    private Long landlordId;

    @Column(nullable = false)
    private String cid;

    @Column(name = "company_name")
    private String companyName;

    private String subdomain;

    @Column(name = "auth_code", columnDefinition = "TEXT")
    private String authCode;

    @Column(name = "api_location")
    private String apiLocation;

    @Column(name = "property_id", nullable = false)
    private String propertyId;

    @Column(name = "occupancy_type")
    private String occupancyType;

    @Column(name = "request_type")
    private String requestType;

    private String referrer;

    @Column(name = "received_at")
    private LocalDateTime receivedAt = LocalDateTime.now();

    private Boolean processed = false;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

