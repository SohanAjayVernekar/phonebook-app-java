package com.phonebook.app.exception;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getDefaultMessage()).orElse("Validation error");return ResponseEntity.badRequest().body(Map.of("detail",m));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<Map<String,String>> illegal(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("detail",e.getMessage()));}
}
