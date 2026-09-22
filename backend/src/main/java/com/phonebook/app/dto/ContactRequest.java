package com.phonebook.app.dto;
import jakarta.validation.constraints.*;
import java.util.Set;
public record ContactRequest(@NotBlank @Size(max=255) String name,@NotBlank @Size(min=7,max=20) String phone_number,@Email String email,@Size(max=10000) String address,String category) {
 public void validate(){
   if(!phone_number.trim().matches("^\\+?[1-9]\\d{6,19}$")) throw new IllegalArgumentException("Phone number must contain 7-20 digits and may start with +");
   if(category!=null && !Set.of("WORK","FAMILY","FRIEND").contains(category)) throw new IllegalArgumentException("Category must be WORK, FAMILY, or FRIEND");
 }
}
