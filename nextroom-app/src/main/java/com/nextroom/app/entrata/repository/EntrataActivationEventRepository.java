package com.nextroom.app.entrata.repository;

import com.nextroom.app.entrata.model.EntrataActivationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntrataActivationEventRepository extends JpaRepository<EntrataActivationEvent, UUID> {
    Optional<EntrataActivationEvent> findByLandlordId(Long landlordId);
    boolean existsByLandlordId(Long landlordId);
    boolean existsByCidAndPropertyId(String cid, String propertyId);
}


