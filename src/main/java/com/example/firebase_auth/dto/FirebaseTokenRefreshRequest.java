package com.example.firebase_auth.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseTokenRefreshRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}