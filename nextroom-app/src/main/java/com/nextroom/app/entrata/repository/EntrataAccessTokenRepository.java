package com.nextroom.app.entrata.repository;

import com.nextroom.app.entrata.model.EntrataAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntrataAccessTokenRepository extends JpaRepository<EntrataAccessToken, Long> {

    Optional<EntrataAccessToken> findByLandlordId(Long landlordId);
}
