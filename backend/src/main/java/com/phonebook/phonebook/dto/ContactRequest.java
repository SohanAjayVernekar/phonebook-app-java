package com.phonebook.phonebook.dto;

import com.phonebook.phonebook.model.ContactCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    String name,

    @NotBlank(message = "Phone number is required")
    @Size(min = 7, max = 20, message = "Phone number must contain 7-20 characters")
    String phoneNumber,

    @Email(message = "Email must be valid")
    String email,

    String address,

    @NotNull(message = "Category is required")
    ContactCategory category
) {}
