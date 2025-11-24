package com.example.firebase_auth.service;


import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@Service
public class AuthenticationService {

    private final UserReposiitory userReposiitory;
    // private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
        private final FirebaseAuthService firebaseAuthService;

    public AuthenticationService(UserReposiitory userReposiitory,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder,

            AuthenticationManager authenticationManager,FirebaseAuthService firebaseAuthService) {
        this.userReposiitory = userReposiitory;

        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.firebaseAuthService=firebaseAuthService;
    }

    public ApiResponse register(@Valid RegisterUserDto registerTeacherDto) {
        Role role = roleRepository.findByRole(RoleEnum.valueOf(registerTeacherDto.getRole()));
        if (role == null) {
            throw new RuntimeException("Role not found: " + registerTeacherDto.getRole());
        }
        String contact = "+977" + registerTeacherDto.getContactNumber();
              try {
            firebaseAuthService.createFirebaseUser(registerTeacherDto.getEmail(), registerTeacherDto.getPassword());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Failed to create Firebase user: " + e.getMessage());
        }

        System.out.println("Role = " + registerTeacherDto.getRole());

        UserModel teachers = UserModel.builder()
                .name(registerTeacherDto.getName())
                .phone(contact)
                .email(registerTeacherDto.getEmail())
                .roleName(registerTeacherDto.getRole())
                .password(passwordEncoder.encode(registerTeacherDto.getPassword()))
                .role(role).build();
        userReposiitory.save(teachers);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
    .withUsername(teachers.getEmail())
    .password(teachers.getPassword())
    .authorities(teachers.getRoleName())
    .build();
        // var refreshToken = jwtService.generateRefresh(new HashMap<>(), teachers);

        return ApiResponse.builder()
                // .refreshToken(refreshToken)
                .build();

    }


          // NEW: Firebase Email/Password Login
    public ApiResponse firebaseLogin(FirebaseLoginRequest firebaseRequest) {
        // FIXED: Remove FirebaseAuthService. prefix
        FirebaseSignInResponse firebaseResponse = 
            firebaseAuthService.loginWithEmailPassword(firebaseRequest.getEmail(), firebaseRequest.getPassword());
        
        // Get or create user in your database
        UserModel user = userReposiitory.findByEmail(firebaseRequest.getEmail());
        if (user == null) {
            // Create user from Firebase
            try {
                FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(firebaseResponse.idToken());
                user = firebaseAuthService.createOrUpdateUserFromFirebase(decodedToken);
            } catch (FirebaseAuthException e) {
                throw new RuntimeException("Failed to verify Firebase token: " + e.getMessage());
            }
        }


        
        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .firebaseToken(firebaseResponse.idToken())
                .refreshToken(firebaseResponse.refreshToken())
                .role(user.getRoleName())
                .userId(user.getId())
                .firebaseUid(firebaseResponse.localId())
                .build();
    }

    // NEW: Refresh Firebase Token
    public ApiResponse refreshFirebaseToken(FirebaseTokenRefreshRequest refreshRequest) {
        // FIXED: Remove FirebaseAuthService. prefix
        RefreshTokenResponse refreshResponse = 
            firebaseAuthService.exchangeRefreshToken(refreshRequest.getRefreshToken());
        
        return ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .firebaseToken(refreshResponse.idToken())
                .refreshToken(refreshResponse.refreshToken())
                .build();
    }

    // NEW: Verify Firebase ID Token
    public ApiResponse verifyFirebaseToken(FirebaseIdTokenRequest tokenRequest) {
        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(tokenRequest.getIdToken());
            UserModel user = firebaseAuthService.createOrUpdateUserFromFirebase(decodedToken);
            
 
            
            return ApiResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .role(user.getRoleName())
                    .userId(user.getId())
                    .firebaseUid(decodedToken.getUid())
                    .build();
                    
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage());
        }
    }
}