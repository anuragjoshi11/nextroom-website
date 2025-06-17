package com.nextroom.app.web.dto;

import lombok.Data;

@Data
public class InviteResponseDTO {
    private String slug;
    private String url;
    private String qrCodePath;
    private String status;
    private String inviterName;
}
