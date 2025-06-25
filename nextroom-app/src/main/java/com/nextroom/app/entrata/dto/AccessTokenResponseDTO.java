package com.nextroom.app.entrata.dto;

import lombok.Data;
import java.util.List;

@Data
public class AccessTokenResponseDTO {
    private Response response;

    @Data
    public static class Response {
        private String code;
        private Result result;
        private Error error;

        @Data
        public static class Result {
            private String access_token;
            private String expires_in;
            private String token_type;
            private List<String> scope;
        }

        @Data
        public static class Error {
            private int code;
            private String message;
        }
    }
}