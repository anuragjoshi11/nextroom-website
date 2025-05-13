package com.nextroom.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DbLogger {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @PostConstruct
    public void logDatasourceUrl() {
        System.out.println("=== Resolved spring.datasource.url === " + datasourceUrl);
    }
}
