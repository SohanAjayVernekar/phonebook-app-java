package com.phonebook.app.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component
public class DatabaseMigration implements CommandLineRunner {
 private final JdbcTemplate jdbc;
 public DatabaseMigration(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @Override public void run(String... args){
  jdbc.execute("ALTER TABLE contacts ADD COLUMN IF NOT EXISTS user_id INTEGER");
  jdbc.execute("CREATE INDEX IF NOT EXISTS ix_contacts_user_id ON contacts(user_id)");
  jdbc.execute("DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='contacts_user_id_fkey') THEN ALTER TABLE contacts ADD CONSTRAINT contacts_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id); END IF; END $$");
  jdbc.execute("UPDATE contacts SET user_id=(SELECT id FROM users ORDER BY id LIMIT 1) WHERE user_id IS NULL AND EXISTS (SELECT 1 FROM users)");
 }
}
