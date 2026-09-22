package com.phonebook.app.dto;
import jakarta.validation.constraints.*;
public record UserRegisterRequest(@NotBlank @Size(min=2,max=255) String name,@NotBlank @Email String email,@NotBlank @Size(min=8,max=128) String password) {}
