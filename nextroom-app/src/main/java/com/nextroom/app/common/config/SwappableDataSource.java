package com.nextroom.app.common.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.springframework.jdbc.datasource.AbstractDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
public class SwappableDataSource extends AbstractDataSource {

    private volatile HikariDataSource target;

    public SwappableDataSource(HikariDataSource initial) {
        this.target = initial;
    }

    public synchronized void swap(HikariDataSource newTarget) {
        this.target = newTarget;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return target.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return target.getConnection(username, password);
    }
}
