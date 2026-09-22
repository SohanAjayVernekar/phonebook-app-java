package com.phonebook.phonebook.dto;

import com.phonebook.phonebook.model.ContactCategory;
import java.time.OffsetDateTime;

public record ContactResponse(
    Integer id,
    String name,
    String phoneNumber,
    String email,
    String address,
    ContactCategory category,
    OffsetDateTime createdAt
) {}
