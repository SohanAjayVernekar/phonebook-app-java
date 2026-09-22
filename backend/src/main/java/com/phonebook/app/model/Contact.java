package com.phonebook.app.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="contacts")
public class Contact {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id") private User user;
    @Column(nullable=false, length=255) private String name;
    @Column(name="phone_number", nullable=false, unique=true, length=20) private String phoneNumber;
    @Column(unique=true, length=255) private String email;
    @Column(columnDefinition="TEXT") private String address;
    @Column(nullable=false, length=20) private String category = "FRIEND";
    @Column(name="created_at", nullable=false, updatable=false) private OffsetDateTime createdAt;
    @PrePersist void onCreate(){ if(createdAt==null) createdAt=OffsetDateTime.now(); if(category==null) category="FRIEND"; }
    public Integer getId(){return id;} public void setId(Integer v){id=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getPhoneNumber(){return phoneNumber;} public void setPhoneNumber(String v){phoneNumber=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public OffsetDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(OffsetDateTime v){createdAt=v;}
}
