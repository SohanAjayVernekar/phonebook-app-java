package com.phonebook.app.dto;
public record TokenResponse(String access_token,String token_type,UserResponse user) {}
