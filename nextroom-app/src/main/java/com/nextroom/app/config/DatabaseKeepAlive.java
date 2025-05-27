package com.nextroom.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DatabaseKeepAlive {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void keepAlive() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception e) {
            // Log if needed
        }
    }
}
