package com.phonebook.app.config;

import com.phonebook.app.model.Contact;
import com.phonebook.app.model.User;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates fake contacts at runtime.
 *
 * No fake record is ever written by hand or pasted in from an
 * LLM: every name, phone number, email, address, category and
 * created-at timestamp below is combined by this class each
 * time the seeder runs.
 */
@Component
public class FakeDataGenerator {

    private static final String[] FIRST_NAMES = {
        "Aarav", "Vihaan", "Aditya", "Arjun", "Rohan",
        "Rahul", "Karan", "Kabir", "Siddharth", "Vikram",
        "Nikhil", "Rajesh", "Amit", "Dev", "Ishaan",
        "Ananya", "Diya", "Ishita", "Aisha", "Priya",
        "Sneha", "Meera", "Kavya", "Nisha", "Rhea",
        "Aanya", "Tara", "Zara", "Kirti", "Pooja",
        "Liam", "Noah", "Ethan", "Oliver", "Lucas",
        "Olivia", "Emma", "Amelia", "Sophia", "Mia"
    };

    private static final String[] LAST_NAMES = {
        "Sharma", "Patel", "Verma", "Mehta", "Nair",
        "Joshi", "Desai", "Kulkarni", "Iyer", "Rao",
        "Singh", "Khan", "Gupta", "Mishra", "Pawar",
        "Naik", "Reddy", "Chawla", "Bansal", "Malhotra",
        "Kapoor", "Chopra", "Das", "Dutta", "Bose"
    };

    private static final String[] CITIES = {
        "Mumbai", "Pune", "Bengaluru", "Delhi", "Hyderabad",
        "Chennai", "Goa", "Nashik", "Jaipur", "Lucknow",
        "Indore", "Kochi", "Surat", "Chandigarh", "Bhopal"
    };

    private static final String[] STREETS = {
        "Rose Lane", "MG Road", "Lake View Road", "Hill Street",
        "Park Avenue", "River Side", "Temple Road", "Station Road",
        "Market Lane", "Garden Street", "Palm Grove", "Sunset Road"
    };

    private static final String[] CATEGORIES = { "WORK", "FAMILY", "FRIEND" };

    /** Realistic gaps so the "has email" filter has something to filter. */
    private static final double EMAIL_CHANCE = 0.85;

    /** Realistic gaps so some cards show without an address. */
    private static final double ADDRESS_CHANCE = 0.8;

    /**
     * Contacts are spread across roughly the last 18 months so
     * the date-range filter returns meaningful subsets.
     */
    private static final long MAX_AGE_DAYS = 540;

    private static final char[] EMAIL_SUFFIX =
        "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private final Random random = new Random();

    /**
     * Builds {@code count} fake contacts for the given user.
     *
     * @param takenPhones phone numbers already used, so generated
     *                    numbers never collide with existing rows
     * @param takenEmails emails already used, same purpose
     */
    public List<Contact> generate(
        User user,
        int count,
        Set<String> takenPhones,
        Set<String> takenEmails
    ) {
        List<Contact> batch = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            batch.add(one(user, takenPhones, takenEmails));
        }

        return batch;
    }

    private Contact one(
        User user,
        Set<String> takenPhones,
        Set<String> takenEmails
    ) {
        String firstName = pick(FIRST_NAMES);
        String lastName = pick(LAST_NAMES);

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setName(firstName + " " + lastName);
        contact.setPhoneNumber(uniquePhone(takenPhones));
        contact.setEmail(uniqueEmail(firstName, lastName, takenEmails));
        contact.setAddress(
            random.nextDouble() < ADDRESS_CHANCE ? randomAddress() : null
        );
        contact.setCategory(pick(CATEGORIES));
        contact.setCreatedAt(randomCreatedAt());
        return contact;
    }

    private String uniquePhone(Set<String> takenPhones) {
        String phone;

        do {
            phone = "9" + String.format("%09d", random.nextInt(1_000_000_000));
        } while (!takenPhones.add(phone));

        return phone;
    }

    private String uniqueEmail(
        String firstName,
        String lastName,
        Set<String> takenEmails
    ) {
        if (random.nextDouble() >= EMAIL_CHANCE) {
            return null;
        }

        String email;

        do {
            email = firstName.toLowerCase()
                + "." + lastName.toLowerCase()
                + "." + suffix()
                + "@example.com";
        } while (!takenEmails.add(email));

        return email;
    }

    private String randomAddress() {
        int houseNumber = random.nextInt(400) + 1;

        return houseNumber
            + " " + pick(STREETS)
            + ", " + pick(CITIES)
            + ", India";
    }

    private OffsetDateTime randomCreatedAt() {
        long daysAgo = random.nextLong(MAX_AGE_DAYS);

        return OffsetDateTime.now()
            .minusDays(daysAgo)
            .minusHours(random.nextInt(24))
            .minusMinutes(random.nextInt(60));
    }

    private String suffix() {
        StringBuilder builder = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            builder.append(EMAIL_SUFFIX[random.nextInt(EMAIL_SUFFIX.length)]);
        }

        return builder.toString();
    }

    private String pick(String[] options) {
        return options[random.nextInt(options.length)];
    }
}
