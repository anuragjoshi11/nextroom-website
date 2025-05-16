package com.nextroom.app.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DatabaseKeepAlive {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseKeepAlive(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void keepAlive() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }
}

