package com.phonebook.app.config;

import com.phonebook.app.model.Contact;
import com.phonebook.app.model.User;
import com.phonebook.app.repository.ContactRepository;
import com.phonebook.app.repository.UserRepository;
import com.phonebook.app.security.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Seeds the documented test account with fake contacts.
 *
 * All fake data is produced by {@link FakeDataGenerator} at
 * runtime — this class only decides how many records are
 * needed and hands the work to the generator.
 */
@Configuration
@Order(2)
public class DataSeeder {
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "Test@12345";
    private static final int TARGET_RECORDS = 1000;

    @Bean
    CommandLineRunner seedTestAccount(
        UserRepository users,
        ContactRepository contacts,
        AuthService auth,
        FakeDataGenerator generator
    ) {
        return args -> seed(users, contacts, auth, generator);
    }

    @Transactional
    void seed(
        UserRepository users,
        ContactRepository contacts,
        AuthService auth,
        FakeDataGenerator generator
    ) {
        User user = users.findByEmail(TEST_EMAIL).orElseGet(() -> {
            User u = new User();
            u.setName("Test User");
            u.setEmail(TEST_EMAIL);
            u.setPasswordHash(auth.hash(TEST_PASSWORD));
            return users.save(u);
        });

        // Always reset the test account password so the documented credentials work
        // even if the account was created by an earlier run with a different password.
        user.setName("Test User");
        user.setPasswordHash(auth.hash(TEST_PASSWORD));
        user = users.saveAndFlush(user);

        // Drop unassigned leftovers from the removed hardcoded seeder.
        List<Contact> leftovers = contacts.findByUserIsNull();
        if (!leftovers.isEmpty()) {
            contacts.deleteAll(leftovers);
        }

        List<Contact> existing = contacts.findAllByUserId(user.getId());

        if (existing.size() >= TARGET_RECORDS) {
            System.out.println(
                "Test account ready: " + TEST_EMAIL
                    + " with " + existing.size() + " contacts."
            );
            return;
        }

        // Never reuse a phone number or email that already exists.
        Set<String> takenPhones = new HashSet<>();
        Set<String> takenEmails = new HashSet<>();
        for (Contact contact : existing) {
            takenPhones.add(contact.getPhoneNumber());
            if (contact.getEmail() != null) {
                takenEmails.add(contact.getEmail());
            }
        }

        int missing = TARGET_RECORDS - existing.size();
        List<Contact> batch =
            generator.generate(user, missing, takenPhones, takenEmails);
        contacts.saveAll(batch);

        System.out.println(
            "Test account ready: " + TEST_EMAIL + " / " + TEST_PASSWORD
                + " with " + contacts.countByUserId(user.getId()) + " contacts."
        );
    }
}
