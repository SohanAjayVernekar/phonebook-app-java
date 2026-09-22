package com.phonebook.app.controller;
import com.phonebook.app.dto.*; import com.phonebook.app.model.User; import com.phonebook.app.repository.UserRepository; import com.phonebook.app.security.*;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final AuthService auth; private final JwtService jwt;
 public AuthController(UserRepository u,AuthService a,JwtService j){users=u;auth=a;jwt=j;}
 @PostMapping("/register") public ResponseEntity<TokenResponse> register(@Valid @RequestBody UserRegisterRequest r){
  String email=r.email().trim().toLowerCase(); if(users.findByEmail(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
  User u=new User();u.setName(r.name().trim());u.setEmail(email);u.setPasswordHash(auth.hash(r.password()));users.save(u);return ResponseEntity.status(201).body(new TokenResponse(jwt.createToken(u.getId()),"bearer",UserResponse.from(u)));
 }
 @PostMapping("/login") public TokenResponse login(@Valid @RequestBody UserLoginRequest r){
  User u=users.findByEmail(r.email().trim().toLowerCase()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
  if(u.getPasswordHash()==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"This account does not have a password. Please use a password account.");
  if(!auth.matches(r.password(),u.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
  return new TokenResponse(jwt.createToken(u.getId()),"bearer",UserResponse.from(u));
 }
 @GetMapping("/me") public UserResponse me(@RequestHeader(value="Authorization",required=false) String header){return UserResponse.from(current(header));}
 @PatchMapping("/me") public UserResponse update(@RequestHeader(value="Authorization",required=false) String header,@Valid @RequestBody UpdateProfileRequest r){User u=current(header);u.setName(r.name().trim());users.save(u);return UserResponse.from(u);}
 @PatchMapping("/me/password") public ResponseEntity<Void> changePassword(@RequestHeader(value="Authorization",required=false) String header,@Valid @RequestBody ChangePasswordRequest r){
  User u=current(header);
  if(u.getPasswordHash()==null||!auth.matches(r.current_password(),u.getPasswordHash()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Current password is incorrect");
  if(r.current_password().equals(r.new_password()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"New password must be different from the current one");
  u.setPasswordHash(auth.hash(r.new_password()));users.save(u);return ResponseEntity.noContent().build();
 }
 private User current(String h){try{if(h==null||!h.startsWith("Bearer "))throw new Exception();return auth.required(jwt.userId(h.substring(7)));}catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Could not validate authentication credentials");}}
}
