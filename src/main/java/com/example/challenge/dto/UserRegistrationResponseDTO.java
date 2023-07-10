package com.example.challenge.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRegistrationResponseDTO {
    private LocalDateTime created;
    private LocalDateTime lastLogin;
    private String token;
    private boolean isActive;
}