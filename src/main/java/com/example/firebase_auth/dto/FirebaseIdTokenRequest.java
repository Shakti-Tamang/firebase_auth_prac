package com.example.firebase_auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseIdTokenRequest {
    @NotBlank(message = "Firebase ID token is required")
    private String idToken;
    
    private String sessionId;
}