package com.phonebook.api;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {
 private final JwtService jwt; public JwtFilter(JwtService jwt){this.jwt=jwt;}
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  var h=req.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")) try{var id=jwt.userId(h.substring(7));var a=new UsernamePasswordAuthenticationToken(id,null,java.util.List.of(new SimpleGrantedAuthority("USER")));SecurityContextHolder.getContext().setAuthentication(a);}catch(Exception ignored){}
  chain.doFilter(req,res);
 }
}
