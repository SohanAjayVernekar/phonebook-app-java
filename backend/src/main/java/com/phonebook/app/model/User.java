package com.phonebook.app.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable=false, length=255) private String name;
    @Column(nullable=false, unique=true, length=255) private String email;
    @Column(name="password_hash", length=255) private String passwordHash;
    @Column(name="google_id", unique=true, length=255) private String googleId;
    @Column(name="created_at", nullable=false, updatable=false)
    private OffsetDateTime createdAt;

    @PrePersist void onCreate() { if (createdAt == null) createdAt = OffsetDateTime.now(); }
    public Integer getId(){return id;} public void setId(Integer id){this.id=id;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public String getGoogleId(){return googleId;} public void setGoogleId(String v){googleId=v;}
    public OffsetDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(OffsetDateTime v){createdAt=v;}
}
