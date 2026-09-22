package com.phonebook.app.dto;
import com.phonebook.app.model.User;
import java.time.OffsetDateTime;
public record UserResponse(Integer id,String name,String email,String google_id,OffsetDateTime created_at) {
 public static UserResponse from(User u){return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getGoogleId(),u.getCreatedAt());}
}
