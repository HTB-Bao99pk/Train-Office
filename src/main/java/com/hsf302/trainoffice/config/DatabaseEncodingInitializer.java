package com.hsf302.trainoffice.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;

@Component
public class DatabaseEncodingInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseEncodingInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        convertColumnToNvarchar("passengers", "full_name", "NVARCHAR(100) NOT NULL");
    }

    private void convertColumnToNvarchar(String tableName, String columnName, String definition) {
        String dataType = jdbcTemplate.queryForObject("""
                SELECT DATA_TYPE
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = ? AND COLUMN_NAME = ?
                """, String.class, tableName, columnName);

        if (!"nvarchar".equalsIgnoreCase(dataType)) {
            try {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " " + definition);
            } catch (DataAccessException ignored) {
                // Keep startup alive; run the ALTER TABLE manually if the database has dependent constraints.
            }
        }
    }
}
