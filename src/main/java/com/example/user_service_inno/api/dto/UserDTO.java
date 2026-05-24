package com.example.user_service_inno.api.dto;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.user_service_inno.entity.PaymentCard;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;


public record UserDTO (

    UUID id,

    @NotBlank(message = "Name cannot be blank")
    String name,

    @NotBlank(message = "surname cannot be blank")
    String surname,

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,

    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    String email,

    Boolean isActive,

    Instant createdAt,
    
    Instant updatedAt,

    List<PaymentCard> cards){}

