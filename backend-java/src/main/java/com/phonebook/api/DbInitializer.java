package com.phonebook.api;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.*;
@Component
public class DbInitializer implements CommandLineRunner {
 private final JdbcTemplate db; private final UserRepo users; private final ContactRepo contacts; private final PasswordEncoder pw;
 DbInitializer(JdbcTemplate d,UserRepo u,ContactRepo c,PasswordEncoder p){db=d;users=u;contacts=c;pw=p;}
 public void run(String... args){
  db.execute("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY,name VARCHAR(255) NOT NULL,email VARCHAR(255) NOT NULL UNIQUE,password_hash VARCHAR(255),google_id VARCHAR(255) UNIQUE,created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)");
  db.execute("CREATE TABLE IF NOT EXISTS contacts (id SERIAL PRIMARY KEY,user_id INTEGER,name VARCHAR(255) NOT NULL,phone_number VARCHAR(20) NOT NULL UNIQUE,email VARCHAR(255) UNIQUE,address TEXT,category VARCHAR(20) NOT NULL DEFAULT 'FRIEND',created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)");
  db.execute("ALTER TABLE contacts ADD COLUMN IF NOT EXISTS user_id INTEGER");
  db.execute("CREATE INDEX IF NOT EXISTS ix_contacts_user_id ON contacts(user_id)");
  var u=users.findByEmail("testuser@example.com").orElseGet(()->{var x=new User();x.name="Test User";x.email="testuser@example.com";x.createdAt=Instant.now();return x;});
  u.name="Test User";u.passwordHash=pw.encode("Test@12345");users.save(u);
  var existing=contacts.findByUserId(u.id);if(existing.size()>=1000)return;
  var rnd=new Random();String[] first={"Aarav","Vihaan","Aditya","Arjun","Rohan","Rahul","Karan","Kabir","Siddharth","Ananya","Diya","Ishita","Aisha","Priya","Sneha","Meera","Kavya","Liam","Noah","Emma","Sophia"};String[] last={"Sharma","Patel","Verma","Mehta","Nair","Joshi","Desai","Kulkarni","Iyer","Rao","Singh","Khan","Gupta","Pawar","Naik","Reddy","Kapoor"};String[] cities={"Mumbai","Pune","Bengaluru","Delhi","Hyderabad","Chennai","Goa","Nashik","Jaipur","Lucknow"};String[] cats={"WORK","FAMILY","FRIEND"};Set<String> phones=new HashSet<>(),emails=new HashSet<>();existing.forEach(c->{phones.add(c.phoneNumber);if(c.email!=null)emails.add(c.email);});
  while(existing.size()<1000){var c=new Contact();String fn=first[rnd.nextInt(first.length)],ln=last[rnd.nextInt(last.length)];c.userId=u.id;c.name=fn+" "+ln;do{c.phoneNumber="9"+String.format("%09d",rnd.nextInt(1_000_000_000));}while(!phones.add(c.phoneNumber));if(rnd.nextDouble()<.85){do{c.email=(fn+"."+ln+"."+Integer.toHexString(rnd.nextInt(65536))+"@example.com").toLowerCase();}while(!emails.add(c.email));}c.address=rnd.nextDouble()<.8?(rnd.nextInt(400)+1)+" MG Road, "+cities[rnd.nextInt(cities.length)]+", India":null;c.category=cats[rnd.nextInt(3)];c.createdAt=Instant.now().minusSeconds(rnd.nextInt(540*86400));contacts.save(c);existing.add(c);}
 }
}
