package com.nextroom.app.entrata.service.impl;

import com.nextroom.app.common.exception.EntrataApiException;
import com.nextroom.app.entrata.config.EntrataTokenManager;
import com.nextroom.app.entrata.dto.ClientInfoDTO;
import com.nextroom.app.entrata.dto.ClientInfoRequestDTO;
import com.nextroom.app.entrata.dto.ClientInfoResponseDTO;
import com.nextroom.app.entrata.model.ClientInfo;
import com.nextroom.app.entrata.repository.ClientInfoRepository;
import com.nextroom.app.entrata.service.ClientInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Optional;

@Service
public class ClientInfoServiceImpl implements ClientInfoService {

    private static final Logger logger = LogManager.getLogger(ClientInfoServiceImpl.class);

    private final ClientInfoRepository clientInfoRepository;
    private final EntrataTokenManager entrataTokenManager;
    private final RestClient restClient;

    @Autowired
    public ClientInfoServiceImpl(ClientInfoRepository clientInfoRepository, EntrataTokenManager entrataTokenManager, RestClient restClient) {
        this.clientInfoRepository = clientInfoRepository;
        this.entrataTokenManager = entrataTokenManager;
        this.restClient = restClient;
    }

    @Override
    public ClientInfoDTO fetchClientInfo(Long landlordId, String subdomain) {
        logger.info("Fetching Entrata client info for landlordId={} and subdomain={}", landlordId, subdomain);

        String accessToken = entrataTokenManager.getAccessToken(landlordId);

        try {
            Optional<ClientInfo> optionalClientInfo = clientInfoRepository.findByLandlordId(landlordId);

            if (optionalClientInfo.isPresent()) {
                logger.info("Client info found in DB for landlordId={}", landlordId);
                return toDto(optionalClientInfo.get());
            }

            logger.info("No client info found in DB for landlordId={}. Making Entrata API call...", landlordId);

            // Build request body
            ClientInfoRequestDTO requestDTO = buildRequestDTO();

            // Make API call
            ClientInfoResponseDTO responseDTO = restClient
                    .post()
                    .uri(String.format("https://%s.entrata.com/api/ils/internetlisting", subdomain))
                    .header("Authorization", String.format("Bearer %s", accessToken))
                    .body(requestDTO)
                    .retrieve()
                    .body(ClientInfoResponseDTO.class);  // throws on 4xx/5xx

            // Validate response
            if (responseDTO == null || responseDTO.getResponse() == null || responseDTO.getResponse().getResult() == null) {
                throw new EntrataApiException("Received empty or malformed response from Entrata");
            }

            ClientInfoResponseDTO.Result result = responseDTO.getResponse().getResult();

            logger.info("Entrata API call successful for landlordId={}: cid={}, company={}",
                    landlordId, result.getCid(), result.getCompanyName());

            ClientInfo newClientInfo = new ClientInfo();
            newClientInfo.setCid(result.getCid());
            newClientInfo.setCompanyName(result.getCompanyName());
            newClientInfo.setSubdomain(result.getSubdomain());
            newClientInfo.setLandlordId(landlordId);

            clientInfoRepository.save(newClientInfo);
            logger.info("Saved Entrata client info to DB for landlordId={} and cid={}", landlordId, result.getCid());

            return toDto(newClientInfo);

        } catch (HttpClientErrorException | HttpServerErrorException httpEx) {
            logger.error("Entrata API HTTP error: status={}, body={}",
                    httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
            throw new EntrataApiException("Entrata API failed: " + httpEx.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error in fetchClientInfo for landlordId={}", landlordId, e);
            throw new RuntimeException("Failed to fetch client info", e);
        }
    }

    private ClientInfoDTO toDto(ClientInfo clientInfo) {
        return new ClientInfoDTO(
                clientInfo.getCid(),
                clientInfo.getCompanyName(),
                clientInfo.getSubdomain()
        );
    }


    private ClientInfoRequestDTO buildRequestDTO() {
        ClientInfoRequestDTO requestDTO = new ClientInfoRequestDTO();

        ClientInfoRequestDTO.Auth auth = new ClientInfoRequestDTO.Auth();
        auth.setType("oauth");

        ClientInfoRequestDTO.Method method = new ClientInfoRequestDTO.Method();
        method.setName("getClientInfo");
        method.setParams(Collections.emptyList());

        requestDTO.setAuth(auth);
        requestDTO.setMethod(method);

        return requestDTO;
    }

}
