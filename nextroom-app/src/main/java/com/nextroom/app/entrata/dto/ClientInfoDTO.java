package com.nextroom.app.entrata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfoDTO {
    private Long cid;
    private String companyName;
    private String subdomain;
}
