package com.phonebook.app.config;

import com.phonebook.app.model.Contact;
import com.phonebook.app.model.User;
import com.phonebook.app.repository.ContactRepository;
import com.phonebook.app.repository.UserRepository;
import com.phonebook.app.security.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "Test@12345";
    private static final int TARGET_RECORDS = 1000;

    @Bean
    CommandLineRunner seedTestAccount(UserRepository users, ContactRepository contacts, AuthService auth) {
        return args -> seed(users, contacts, auth);
    }

    @Transactional
    void seed(UserRepository users, ContactRepository contacts, AuthService auth) {
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
        user = users.save(user);

        // If an earlier version of the seeder created unassigned contacts,
        // attach them to the test account first.
        long assigned = contacts.countByUserId(user.getId());
        if (assigned < TARGET_RECORDS) {
            List<Contact> unassigned = contacts.findByUserIsNull();
            int needed = TARGET_RECORDS - (int) assigned;
            for (int i = 0; i < Math.min(needed, unassigned.size()); i++) {
                unassigned.get(i).setUser(user);
            }
            if (!unassigned.isEmpty()) contacts.saveAll(unassigned.subList(0, Math.min(needed, unassigned.size())));
            assigned = contacts.countByUserId(user.getId());
        }

        if (assigned >= TARGET_RECORDS) {
            System.out.println("Test account ready: " + TEST_EMAIL + " with " + assigned + " contacts.");
            return;
        }

        int missing = TARGET_RECORDS - (int) assigned;
        List<Contact> batch = new ArrayList<>(missing);
        String[] firstNames = {"Aarav","Vihaan","Aditya","Arjun","Rohan","Rahul","Karan","Kabir","Ananya","Diya","Ishita","Aisha","Priya","Sneha","Meera","Kavya"};
        String[] lastNames = {"Sharma","Patel","Verma","Mehta","Nair","Joshi","Desai","Kulkarni","Iyer","Rao","Singh","Khan","Gupta","Mishra","Pawar","Naik"};
        String[] cities = {"Mumbai","Pune","Bengaluru","Delhi","Hyderabad","Chennai","Goa","Nashik"};
        String[] categories = {"WORK","FAMILY","FRIEND"};

        for (int i = 1; i <= missing; i++) {
            int n = (int) assigned + i;
            Contact c = new Contact();
            c.setUser(user);
            c.setName(firstNames[(n - 1) % firstNames.length] + " " + lastNames[((n - 1) / firstNames.length) % lastNames.length] + " " + n);
            c.setPhoneNumber("9" + String.format("%09d", n));
            c.setEmail("contact" + n + "@example.com");
            c.setAddress(n + " Main Street, " + cities[(n - 1) % cities.length] + ", India");
            c.setCategory(categories[(n - 1) % categories.length]);
            batch.add(c);
        }
        contacts.saveAll(batch);
        System.out.println("Test account ready: " + TEST_EMAIL + " / " + TEST_PASSWORD + " with " + contacts.countByUserId(user.getId()) + " contacts.");
    }
}
