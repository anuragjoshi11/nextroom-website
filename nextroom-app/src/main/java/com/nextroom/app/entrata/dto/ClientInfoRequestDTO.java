package com.nextroom.app.entrata.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClientInfoRequestDTO {
    private Auth auth;
    private Method method;

    @Data
    public static class Auth {
        private String type;
    }

    @Data
    public static class Method {
        private String name;
        private List<String> params;
    }
}
