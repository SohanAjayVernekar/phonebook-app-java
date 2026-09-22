package com.phonebook.app.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Service
public class JwtService {
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-minutes:60}") long minutes){
  String s=secret.length()>=32?secret:"change-this-secret-key-in-production-please-use-a-long-random-value"; key=Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8)); expiration=minutes*60_000L;
 }
 public String createToken(Integer userId){Date now=new Date();return Jwts.builder().subject(String.valueOf(userId)).issuedAt(now).expiration(new Date(now.getTime()+expiration)).signWith(key).compact();}
 public Integer userId(String token){return Integer.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());}
}
