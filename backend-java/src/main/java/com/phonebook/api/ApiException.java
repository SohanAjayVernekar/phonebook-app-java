package com.phonebook.api;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
public class ApiException extends RuntimeException {
 public final HttpStatus status; public ApiException(HttpStatus s,String m){super(m);status=s;}
 @RestControllerAdvice static class Handler {
  @ExceptionHandler(ApiException.class) ResponseEntity<?> api(ApiException e){return ResponseEntity.status(e.status).body(Map.of("detail",e.getMessage(),"message",e.getMessage()));}
  @ExceptionHandler(Exception.class) ResponseEntity<?> other(Exception e){return ResponseEntity.status(500).body(Map.of("detail","Internal server error","message","Internal server error"));}
 }
}
