package com.example.user_service_inno.api.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentCardDTO (

    UUID id,
    
    UUID user_id,

    @NotNull(message = "Card number is required")
    @Min(value = 1000000000000000L, message = "Card number must be 16 digits")
    @Max(value = 9999999999999999L, message = "Card number must be 16 digits")
    Long number,

    @NotBlank(message = "Holder cannot be blank")
    String holder,

    Instant expirationDate,

    Boolean isActive,

    Instant createdAt,
    
    Instant updatedAt

){}
