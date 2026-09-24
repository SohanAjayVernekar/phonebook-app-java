package com.phonebook.api;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="contacts")
public class Contact {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Integer id;
  @Column(name="user_id") public Integer userId;
  @Column(nullable=false,length=255) public String name;
  @Column(name="phone_number",nullable=false,unique=true,length=20) public String phoneNumber;
  @Column(unique=true,length=255) public String email;
  @Column(columnDefinition="text") public String address;
  @Column(nullable=false,length=20) public String category="FRIEND";
  @Column(name="created_at",nullable=false) public Instant createdAt;
}
