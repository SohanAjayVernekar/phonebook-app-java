package com.phonebook.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import java.util.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepo users; private final PasswordEncoder pw; private final JwtService jwt;
 AuthController(UserRepo u,PasswordEncoder p,JwtService j){users=u;pw=p;jwt=j;}
 private String text(String s,String msg){if(s==null||s.isBlank())throw new ApiException(HttpStatus.BAD_REQUEST,msg);return s.trim();}
 @PostMapping("/register") ResponseEntity<?> register(@RequestBody Dto.Register r){
  String name=text(r.name(),"must not be blank"),email=text(r.email(),"must not be blank").toLowerCase(),pass=text(r.password(),"must not be blank");
  if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))throw new ApiException(HttpStatus.BAD_REQUEST,"must be a well-formed email address");
  if(name.length()<2||name.length()>255)throw new ApiException(HttpStatus.BAD_REQUEST,"size must be between 2 and 255");
  if(pass.length()<8||pass.length()>128)throw new ApiException(HttpStatus.BAD_REQUEST,"size must be between 8 and 128");
  if(users.findByEmail(email).isPresent())throw new ApiException(HttpStatus.CONFLICT,"Email is already registered");
  var u=new User();u.name=name;u.email=email;u.passwordHash=pw.encode(pass);u.createdAt=Instant.now();users.save(u);
  return ResponseEntity.status(201).body(new Dto.Token(jwt.create(u.id),"bearer",Dto.user(u)));
 }
 @PostMapping("/login") Object login(@RequestBody Dto.Login r){
  var email=text(r.email(),"must not be blank").toLowerCase();var pass=text(r.password(),"must not be blank");
  var u=users.findByEmail(email).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
  if(u.passwordHash==null||!pw.matches(pass,u.passwordHash))throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
  return new Dto.Token(jwt.create(u.id),"bearer",Dto.user(u));
 }
 @GetMapping("/me") Object me(org.springframework.security.core.Authentication a){return Dto.user(user(a));}
 @PatchMapping("/me") Object profile(@RequestBody Dto.Profile r,org.springframework.security.core.Authentication a){
  var u=user(a);var n=text(r.name(),"Name is required");if(n.length()<2||n.length()>255)throw new ApiException(HttpStatus.BAD_REQUEST,"Name must be between 2 and 255 characters");u.name=n;users.save(u);return Dto.user(u);
 }
 @PatchMapping("/me/password") ResponseEntity<?> password(@RequestBody Dto.Password r,org.springframework.security.core.Authentication a){
  var u=user(a);var cur=text(r.current_password(),"Current password is required");if(u.passwordHash==null||!pw.matches(cur,u.passwordHash))throw new ApiException(HttpStatus.BAD_REQUEST,"Current password is incorrect");
  var n=text(r.new_password(),"New password is required");if(n.length()<8||n.length()>128)throw new ApiException(HttpStatus.BAD_REQUEST,"Password must be between 8 and 128 characters");if(pw.matches(n,u.passwordHash))throw new ApiException(HttpStatus.BAD_REQUEST,"New password must be different from the current one");u.passwordHash=pw.encode(n);users.save(u);return ResponseEntity.noContent().build();
 }
 private User user(org.springframework.security.core.Authentication a){return users.findById((Integer)a.getPrincipal()).orElseThrow(()->new ApiException(HttpStatus.BAD_REQUEST,"Could not validate authentication credentials"));}
}
