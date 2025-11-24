package com.example.firebase_auth.service;


import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import com.example.firebase_auth.ApiResponse.ApiResponse;
import com.example.firebase_auth.dto.FirebaseIdTokenRequest;
import com.example.firebase_auth.dto.FirebaseLoginRequest;
import com.example.firebase_auth.dto.FirebaseTokenRefreshRequest;
import com.example.firebase_auth.dto.RegisterUserDto;
import com.example.firebase_auth.enums.RoleEnum;
import com.example.firebase_auth.model.Role;
import com.example.firebase_auth.model.UserModel;
import com.example.firebase_auth.records.FirebaseSignInResponse;
import com.example.firebase_auth.records.RefreshTokenResponse;
import com.example.firebase_auth.repo.RoleRepository;
import com.example.firebase_auth.repo.UserReposiitory;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.validation.Valid;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserReposiitory userReposiitory;
    private final RoleRepository roleRepository;
    private final FirebaseAuthService firebaseAuthService;

    public ApiResponse register(@Valid RegisterUserDto registerUserDto) {
        Role role = roleRepository.findByRole(RoleEnum.valueOf(registerUserDto.getRole()));
        if (role == null) {
            throw new RuntimeException("Role not found: " + registerUserDto.getRole());
        }

        String contact = "+977" + registerUserDto.getContactNumber();
        
        try {
            firebaseAuthService.createFirebaseUser(registerUserDto.getEmail(), registerUserDto.getPassword());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Failed to create Firebase user: " + e.getMessage());
        }

        // Create user in local database
        UserModel user = UserModel.builder()
                .name(registerUserDto.getName())
                .phone(contact)
                .email(registerUserDto.getEmail())
                .roleName(registerUserDto.getRole())
                .password("") // No password for Firebase users
                .role(role)
                .build();
        
        userReposiitory.save(user);

        return ApiResponse.builder()
                .message("User registered successfully with Firebase")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    public ApiResponse firebaseLogin(FirebaseLoginRequest firebaseRequest) {
        FirebaseSignInResponse firebaseResponse = 
            firebaseAuthService.loginWithEmailPassword(firebaseRequest.getEmail(), firebaseRequest.getPassword());
        
        // Get or create user in your database
        UserModel user = userReposiitory.findByEmail(firebaseRequest.getEmail());
        if (user == null) {
            // Create user from Firebase with default role
            try {
                FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(firebaseResponse.idToken());
                user = firebaseAuthService.createOrUpdateUserFromFirebase(decodedToken, "USER");
            } catch (FirebaseAuthException e) {
                throw new RuntimeException("Failed to verify Firebase token: " + e.getMessage());
            }
        }

        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .firebaseToken(firebaseResponse.idToken())
                .role(user.getRoleName())
                .userId(user.getId())
                .firebaseUid(firebaseResponse.localId())
                .build();
    }

    public ApiResponse refreshFirebaseToken(FirebaseTokenRefreshRequest refreshRequest) {
        RefreshTokenResponse refreshResponse = 
            firebaseAuthService.exchangeRefreshToken(refreshRequest.getRefreshToken());
        
        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .firebaseToken(refreshResponse.idToken())
                .refreshToken(refreshResponse.refreshToken())
                .build();
    }

    public ApiResponse verifyFirebaseToken(FirebaseIdTokenRequest tokenRequest) {
        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(tokenRequest.getIdToken());
            UserModel user = userReposiitory.findByEmail(decodedToken.getEmail());
            
            if (user == null) {
                user = firebaseAuthService.createOrUpdateUserFromFirebase(decodedToken, "USER");
            }
            
            return ApiResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Token verified successfully")
                    .role(user.getRoleName())
                    .userId(user.getId())
                    .firebaseUid(decodedToken.getUid())
                    .build();
                    
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage());
        }
    }
}
