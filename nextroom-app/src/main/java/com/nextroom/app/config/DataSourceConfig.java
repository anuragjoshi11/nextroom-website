package com.nextroom.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableScheduling
public class DataSourceConfig {

    private static final Logger logger = LogManager.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BASE_DELAY_MS = 2000;

    private volatile SwappableDataSource swappableDataSource;
    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);

    @Bean
    public DataSource dataSource() {
        HikariDataSource pool = createDataSourceWithRetries();
        this.swappableDataSource = new SwappableDataSource(pool);
        return this.swappableDataSource;
    }

    @Scheduled(fixedDelay = 55 * 60 * 1000, initialDelay = 55 * 60 * 1000)
    public void refreshDataSource() {
        logger.info("Starting refreshDataSource at {}", java.time.Instant.now());

        if (!refreshInProgress.compareAndSet(false, true)) {
            logger.warn("Refresh already in progress. Skipping this cycle.");
            return;
        }

        try {
            HikariDataSource newPool = createNewDataSource(0);
            HikariDataSource oldPool = swappableDataSource.getTarget();

            swappableDataSource.swap(newPool);
            logger.info("Swapped in new connection pool with refreshed IAM token.");

            Thread.sleep(1000); // allow graceful drain of any active threads

            oldPool.close();
            logger.info("Closed previous connection pool.");
        } catch (Exception e) {
            logger.error("Failed to refresh DataSource: {}", e.getMessage(), e);
        } finally {
            refreshInProgress.set(false);
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (swappableDataSource != null) {
                swappableDataSource.getTarget().close();
            }
        } catch (Exception e) {
            logger.error("Error during DataSource shutdown: {}", e.getMessage(), e);
        }
    }

    private HikariDataSource createDataSourceWithRetries() {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return createNewDataSource(attempt);
            } catch (SQLException | IOException e) {
                lastException = e;
                logger.warn("Attempt {} failed to create DataSource: {}", attempt, e.getMessage());
            }

            try {
                Thread.sleep(RETRY_BASE_DELAY_MS * (1L << (attempt - 1)));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new IllegalStateException("Could not create DataSource after " + MAX_RETRIES + " attempts", lastException);
    }

    private HikariDataSource createNewDataSource(int attempt) throws IOException, SQLException {
        String token = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"))
                .refreshAccessToken()
                .getTokenValue();

        HikariDataSource dataSource = getHikariDataSource(token);

        try {
            if (attempt > 0) {
                logger.info("Attempt {}: Establishing connection to PostgreSQL for validation...", attempt);
            } else {
                logger.info("Establishing connection to PostgreSQL for scheduled refresh...");
            }

            dataSource.getConnection().close(); // validate connection

            if (attempt > 0) {
                logger.info("Attempt {}: Successfully established a connection to PostgreSQL.", attempt);
            } else {
                logger.info("Successfully established a connection to PostgreSQL.");
            }

        } catch (SQLException e) {
            if (attempt > 0) {
                logger.error("Attempt {}: Failed to validate connection to PostgreSQL: {}", attempt, e.getMessage(), e);
            } else {
                logger.error("Failed to validate connection to PostgreSQL: {}", e.getMessage(), e);
            }
            dataSource.close();
            throw e;
        }

        return dataSource;
    }

    private HikariDataSource getHikariDataSource(String token) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(token);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);          // 30 seconds
        config.setIdleTimeout(300_000);               // 5 minutes
        config.setMaxLifetime(1_500_000);             // 25 minutes
        config.setLeakDetectionThreshold(10_000);     // 10 seconds
        config.setPoolName("HikariPool-PostgreSQL");

        return new HikariDataSource(config);
    }
}