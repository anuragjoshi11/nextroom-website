package com.nextroom.app.entrata.repository;

import com.nextroom.app.entrata.model.ClientInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientInfoRepository extends JpaRepository<ClientInfo, Long> {
    Optional<ClientInfo> findByLandlordId(Long landlordId);
}
