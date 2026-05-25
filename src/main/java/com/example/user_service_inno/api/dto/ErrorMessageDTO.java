package com.example.user_service_inno.api.dto;

import java.time.LocalDateTime;

public record ErrorMessageDTO(
        int statusCode,
        LocalDateTime timestamp,
        String message,
        String description
) {}
