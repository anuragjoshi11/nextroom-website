package com.nextroom.app.web.dto;

import lombok.Data;

@Data
public class InviteValidationResponseDTO {
    private Long inviteId;
    private String slug;
    private String status;
    private String inviterName;
}
