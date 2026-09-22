package com.phonebook.app.dto;
import jakarta.validation.constraints.*;
public record UserLoginRequest(@NotBlank @Email String email,@NotBlank String password) {}
