package com.nextroom.app.web.dto;

import lombok.Data;

@Data
public class InviteActionResponseDTO {
    private Long inviteId;
    private String status;
    private String message;
}

