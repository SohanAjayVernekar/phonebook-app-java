package com.phonebook.app.dto;
import com.phonebook.app.model.Contact;
import java.time.OffsetDateTime;
public record ContactResponse(Integer id,String name,String phone_number,String email,String address,String category,OffsetDateTime created_at) {
 public static ContactResponse from(Contact c){return new ContactResponse(c.getId(),c.getName(),c.getPhoneNumber(),c.getEmail(),c.getAddress(),c.getCategory(),c.getCreatedAt());}
}
