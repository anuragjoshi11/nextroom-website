package com.nextroom.app.entrata.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccessTokenRequestDTO {
    private Auth auth;
    private Method method;

    @Data
    public static class Auth {
        private String type;
        private String code;
        private String grant_type;
        private String client_id;
        private String client_secret;
    }

    @Data
    public static class Method {
        private String name;
        private List<String> params;
    }
}
