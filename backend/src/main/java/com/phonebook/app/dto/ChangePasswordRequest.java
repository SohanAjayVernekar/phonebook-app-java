package com.phonebook.app.dto;
import jakarta.validation.constraints.*;
public record ChangePasswordRequest(@NotBlank(message="Current password is required") String current_password,@NotBlank(message="New password is required") @Size(min=8,max=128,message="New password must be between 8 and 128 characters") String new_password) {}
