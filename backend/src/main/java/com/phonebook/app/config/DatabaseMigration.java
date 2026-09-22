package com.phonebook.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabaseMigration implements CommandLineRunner {
    private final JdbcTemplate jdbc;

    public DatabaseMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        // Make the user table explicit so the test account works even on a fresh
        // PostgreSQL volume and does not depend on Hibernate's startup ordering.
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                password_hash VARCHAR(255),
                google_id VARCHAR(255) UNIQUE,
                created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """);

        jdbc.execute("ALTER TABLE contacts ADD COLUMN IF NOT EXISTS user_id INTEGER");
        jdbc.execute("CREATE INDEX IF NOT EXISTS ix_contacts_user_id ON contacts(user_id)");
        jdbc.execute("""
            DO $$ BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_constraint WHERE conname='contacts_user_id_fkey'
                ) THEN
                    ALTER TABLE contacts
                    ADD CONSTRAINT contacts_user_id_fkey
                    FOREIGN KEY (user_id) REFERENCES users(id);
                END IF;
            END $$
            """);
    }
}
