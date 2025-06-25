package com.nextroom.app.entrata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "entrata_client_info", uniqueConstraints = { @UniqueConstraint(columnNames = "cid"), @UniqueConstraint(columnNames = "landlordId")})
public class ClientInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cid")
    private Long cid;

    @Column(name = "landlord_id")
    private Long landlordId;

    @Column(name="company_name")
    private String companyName;

    @Column(name = "subdomain")
    private String subdomain;

    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")  // Column name in lowercase
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
