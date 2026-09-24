package com.phonebook.api;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Integer id;
  @Column(nullable=false,length=255) public String name;
  @Column(nullable=false,unique=true,length=255) public String email;
  @Column(name="password_hash",length=255) public String passwordHash;
  @Column(name="google_id",unique=true,length=255) public String googleId;
  @Column(name="created_at",nullable=false) public Instant createdAt;
}
