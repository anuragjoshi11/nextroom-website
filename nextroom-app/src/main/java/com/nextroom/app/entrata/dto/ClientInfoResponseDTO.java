package com.nextroom.app.entrata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfoResponseDTO {
    private ResponseWrapper response;

    @Data
    public static class ResponseWrapper {
        private String code;
        private Result result;
    }

    @Data
    public static class Result {
        private Long cid;
        private String companyName;
        private String subdomain;
    }
}
