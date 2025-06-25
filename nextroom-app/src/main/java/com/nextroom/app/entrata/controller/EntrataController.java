package com.nextroom.app.entrata.controller;

import com.nextroom.app.entrata.dto.ClientInfoDTO;
import com.nextroom.app.entrata.dto.EntrataActivationEventDTO;
import com.nextroom.app.entrata.service.ClientInfoService;
import com.nextroom.app.entrata.service.EntrataActivationEventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entrata")
@CrossOrigin(origins = {"http://localhost:5000", "https://nextroom-frontend.uc.r.appspot.com", "https://www.nextroom.ca", "https://nextroom.ca", "https://dev-nextroom-frontend.ue.r.appspot.com"})
public class EntrataController {

    private final ClientInfoService clientInfoService;
    private final EntrataActivationEventService entrataActivationEventService;

    @Autowired
    public EntrataController(ClientInfoService clientInfoService, EntrataActivationEventService entrataActivationEventService) {
        this.clientInfoService = clientInfoService;
        this.entrataActivationEventService = entrataActivationEventService;
    }

    @GetMapping("/client-info")
    public ResponseEntity<ClientInfoDTO> getClientInfo( @RequestParam Long landlordId, @RequestParam String subdomain
    ) {
        ClientInfoDTO clientInfo = clientInfoService.fetchClientInfo(landlordId, subdomain);
        return ResponseEntity.ok(clientInfo);
    }

    @GetMapping("/api")
    public ResponseEntity<String> receiveWebhook(
            @RequestParam("cid") String cid,
            @RequestParam(value = "company_name", required = false) String companyName,
            @RequestParam(value = "subdomain", required = false) String subdomain,
            @RequestParam(value = "auth_code", required = false) String authCode,
            @RequestParam(value = "api_location", required = false) String apiLocation,
            @RequestParam(value = "property_id", required = false) String propertyId,
            @RequestParam(value = "occupancy_type", required = false) String occupancyType,
            @RequestParam(value = "request_type", required = false) String requestType,
            @RequestParam(value = "referrer", required = false) String referrer
    ) {
        EntrataActivationEventDTO dto = new EntrataActivationEventDTO();
        dto.setCid(cid);
        dto.setCompanyName(companyName);
        dto.setSubdomain(subdomain);
        dto.setAuthCode(authCode);
        dto.setApiLocation(apiLocation);
        dto.setPropertyId(propertyId);
        dto.setOccupancyType(occupancyType);
        dto.setRequestType(requestType);
        dto.setReferrer(referrer);

        String message = entrataActivationEventService.saveEvent(dto);
        return ResponseEntity.ok(message);
    }

}
