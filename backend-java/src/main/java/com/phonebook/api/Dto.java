package com.phonebook.api;
import java.time.Instant;
public final class Dto {
 public record Register(String name,String email,String password){}
 public record Login(String email,String password){}
 public record Profile(String name){}
 public record Password(String current_password,String new_password){}
 public record Token(String access_token,String token_type,Object user){}
 public record ContactReq(String name,String phone_number,String email,String address,String category){}
 public record Bulk(java.util.List<Integer> ids){}
 public static Object user(User u){return new java.util.LinkedHashMap<String,Object>(){{put("id",u.id);put("name",u.name);put("email",u.email);put("google_id",u.googleId);put("created_at",u.createdAt);}};}
 public static Object contact(Contact c){return new java.util.LinkedHashMap<String,Object>(){{put("id",c.id);put("name",c.name);put("phone_number",c.phoneNumber);put("email",c.email);put("address",c.address);put("category",c.category);put("created_at",c.createdAt);}};}
 private Dto(){}
}
