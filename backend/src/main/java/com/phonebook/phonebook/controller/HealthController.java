package com.phonebook.phonebook.controller;

import com.phonebook.phonebook.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(
        DataSource dataSource
    ) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public HealthResponse health() {
        String database = "online";

        try (
            Connection connection =
                dataSource.getConnection()
        ) {
            if (!connection.isValid(2)) {
                database = "offline";
            }
        } catch (Exception ignored) {
            database = "offline";
        }

        return new HealthResponse(
            "online",
            database
        );
    }
}
