package com.phonebook.app.security;
import com.phonebook.app.model.User;
import com.phonebook.app.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
 private final UserRepository users; private final Argon2PasswordEncoder encoder=Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
 public AuthService(UserRepository users){this.users=users;}
 public String hash(String p){return encoder.encode(p);} public boolean matches(String p,String h){return encoder.matches(p,h);}
 public User required(Integer id){return users.findById(id).orElseThrow(()->new IllegalArgumentException("Could not validate authentication credentials"));}
}
