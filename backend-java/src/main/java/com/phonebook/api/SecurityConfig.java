package com.phonebook.api;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){ return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8(); }
 @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
  return http.csrf(c->c.disable()).cors(c->c.configurationSource(r->{var x=new CorsConfiguration();x.setAllowedOriginPatterns(List.of("*"));x.setAllowedMethods(List.of("*"));x.setAllowedHeaders(List.of("*"));return x;}))
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/register","/api/auth/login","/api/health","/swagger-ui/**","/v3/api-docs/**").permitAll().anyRequest().authenticated())
   .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"detail\":\"Could not validate authentication credentials\",\"message\":\"Could not validate authentication credentials\"}");}).accessDeniedHandler((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"detail\":\"Could not validate authentication credentials\",\"message\":\"Could not validate authentication credentials\"}");}))
   .addFilterBefore(jwtFilter,org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class).build();
 }
}
