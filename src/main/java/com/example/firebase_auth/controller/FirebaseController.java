package com.example.firebase_auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.firebase_auth.ApiResponse.ApiResponse;
import com.example.firebase_auth.dto.FirebaseIdTokenRequest;
import com.example.firebase_auth.dto.FirebaseLoginRequest;
import com.example.firebase_auth.dto.FirebaseTokenRefreshRequest;
import com.example.firebase_auth.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/firebase")
@RequiredArgsConstructor
public class FirebaseController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> firebaseLogin(@Valid @RequestBody

    FirebaseLoginRequest request) {
        ApiResponse response = authenticationService.firebaseLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@Valid @RequestBody FirebaseTokenRefreshRequest request) {
        ApiResponse response = authenticationService.refreshFirebaseToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<ApiResponse> verifyToken(@Valid @RequestBody FirebaseIdTokenRequest request) {
        ApiResponse response = authenticationService.verifyFirebaseToken(request);
        return ResponseEntity.ok(response);
    }

}
