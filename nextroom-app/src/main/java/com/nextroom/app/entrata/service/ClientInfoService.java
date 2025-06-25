package com.nextroom.app.entrata.service;

import com.nextroom.app.entrata.dto.ClientInfoDTO;
import com.nextroom.app.entrata.dto.ClientInfoResponseDTO;

public interface ClientInfoService {

    public ClientInfoDTO fetchClientInfo (Long landlordId, String subdomain);
}
