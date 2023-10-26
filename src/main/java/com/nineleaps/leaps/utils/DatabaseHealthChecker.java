package com.nineleaps.leaps.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Slf4j
public class DatabaseHealthChecker implements ApplicationRunner {

    private Connection connection;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUsername;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        establishDatabaseConnection();
        checkDatabaseHealth();
    }

    private void establishDatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
        log.info("Database Connection Established. Successfully");
    }

    @PreDestroy
    public void preDestroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Database connection closed.");
            }
        } catch (SQLException e) {
            log.error("Error closing database connection: " + e.getMessage());
        }
    }

    public void checkDatabaseHealth() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        log.info("Database Product Name: " + metaData.getDatabaseProductName());
        log.info("Database Product Version: " + metaData.getDatabaseProductVersion());
        log.info("DataBase URL :" + metaData.getURL());
        log.info("Database UserName :" + metaData.getUserName());

        // If we successfully connect to the database, log the success message
        log.info("Database is healthy");
    }
}