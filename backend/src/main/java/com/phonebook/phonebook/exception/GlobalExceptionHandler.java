package com.phonebook.phonebook.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(
        EntityNotFoundException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("detail", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflict(
        DataIntegrityViolationException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of(
                "detail",
                ex.getMessage() == null
                    ? "Contact with the provided details already exists"
                    : ex.getMessage()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(
        MethodArgumentNotValidException ex
    ) {
        var detail =
            ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    Map.of(
                        "field",
                        error.getField(),
                        "msg",
                        error.getDefaultMessage()
                    )
                )
                .toList();

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(Map.of("detail", detail));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(
        IllegalArgumentException ex
    ) {
        return ResponseEntity
            .badRequest()
            .body(Map.of(
                "detail",
                ex.getMessage()
            ));
    }
}
