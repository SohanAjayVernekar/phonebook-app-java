package com.phonebook.phonebook.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20, unique = true)
    private String phoneNumber;

    @Column(length = 255, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactCategory category = ContactCategory.FRIEND;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (category == null) {
            category = ContactCategory.FRIEND;
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public Contact() {}

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public ContactCategory getCategory() { return category; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setCategory(ContactCategory category) { this.category = category; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
