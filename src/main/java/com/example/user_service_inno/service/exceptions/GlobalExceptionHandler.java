package com.example.user_service_inno.service.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.user_service_inno.api.dto.ErrorMessageDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {


        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorMessageDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        ErrorMessageDTO errorDescription = new ErrorMessageDTO(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorDescription, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorMessageDTO> handleValidationException(
                MethodArgumentNotValidException ex, WebRequest request) {
        
        String detailedErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("'%s' - %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        ErrorMessageDTO errorDescription = new ErrorMessageDTO(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                "Validation failed: " + detailedErrors,
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorDescription, HttpStatus.BAD_REQUEST);
        }
    
    @ExceptionHandler(CardLimitExceededException.class)
    public ResponseEntity<ErrorMessageDTO> handleCardLimitExceeded(
            CardLimitExceededException ex, WebRequest request) {
        
        ErrorMessageDTO error = new ErrorMessageDTO(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorMessageDTO errorDescription = new ErrorMessageDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                "Internal Server Error: Something went wrong.",
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorDescription, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
