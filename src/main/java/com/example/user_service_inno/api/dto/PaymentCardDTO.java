package com.example.user_service_inno.api.dto;

import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentCardDTO (

    UUID id,
    
    UUID userId,

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number should contain 16 digits")
    String number,

    @NotBlank(message = "Holder cannot be blank")
    String holder,

    @FutureOrPresent(message = "Срок действия карты не может быть в прошлом")
    YearMonth expirationDate,

    Boolean isActive,

    Instant createdAt,
    
    Instant updatedAt
){}
