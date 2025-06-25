package com.nextroom.app.entrata.service;

import com.nextroom.app.entrata.dto.EntrataActivationEventDTO;
import com.nextroom.app.entrata.model.EntrataActivationEvent;

import java.util.Optional;
import java.util.UUID;

public interface EntrataActivationEventService {

    public String saveEvent(EntrataActivationEventDTO dto);

    public Optional<EntrataActivationEvent> getByLandlordId(Long landlordId);

    public boolean existsByLandlordId(Long landlordId);
}


