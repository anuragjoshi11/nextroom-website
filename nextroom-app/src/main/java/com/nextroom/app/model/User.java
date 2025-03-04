package com.nextroom.app.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Users")
@JsonPropertyOrder({"userId", "firstName", "lastName", "email", "username", "password", "status", "role", "createdDate", "updatedDate"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Long userId;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Email", unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "Status", nullable = false)
    private Boolean status;

    @Column(name = "Role", nullable = false)
    private String role;

    @Column(name = "CreatedDate", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @Column(name = "UpdatedDate")
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
