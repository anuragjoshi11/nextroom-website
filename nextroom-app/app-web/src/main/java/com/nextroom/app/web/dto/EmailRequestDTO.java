package com.nextroom.app.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmailRequestDTO {
    private List<String> selectedCompanies;
    private List<String> selectedProperties;
}
