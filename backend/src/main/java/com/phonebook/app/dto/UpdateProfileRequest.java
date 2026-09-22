package com.phonebook.app.dto;
import jakarta.validation.constraints.*;
public record UpdateProfileRequest(@NotBlank(message="Name is required") @Size(min=2,max=255,message="Name must be between 2 and 255 characters") String name) {}
