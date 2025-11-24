package com.example.firebase_auth.service;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.example.firebase_auth.config.FirebaseConfig;
import com.example.firebase_auth.model.UserModel;
import com.example.firebase_auth.records.FirebaseSignInRequest;
import com.example.firebase_auth.records.FirebaseSignInResponse;
import com.example.firebase_auth.records.RefreshTokenRequest;
import com.example.firebase_auth.records.RefreshTokenResponse;
import com.example.firebase_auth.repo.UserReposiitory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirebaseAuthService implements UserDetailsService {

    private final FirebaseConfig firebaseConfig;
    private final FirebaseAuth firebaseAuth;
    private final UserReposiitory userRepository;

    private static final String INVALID_CREDENTIALS_ERROR = "INVALID_LOGIN_CREDENTIALS";
    private static final String INVALID_REFRESH_TOKEN_ERROR = "INVALID_REFRESH_TOKEN";
    private static final String API_KEY_PARAM = "key";
    private static final String SIGN_IN_BASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";
    private static final String REFRESH_TOKEN_BASE_URL = "https://securetoken.googleapis.com/v1/token";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    public void createFirebaseUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(true);

        try {
            firebaseAuth.createUser(request);
        } catch (FirebaseAuthException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("EMAIL_EXISTS")) {
                throw new RuntimeException("Account with given email already exists in Firebase");
            }
            throw exception;
        }
    }

    public FirebaseSignInResponse loginWithEmailPassword(String email, String password) {
        FirebaseSignInRequest requestBody = new FirebaseSignInRequest(email, password, true);
        return sendSignInRequest(requestBody);
    }

    public RefreshTokenResponse exchangeRefreshToken(String refreshToken) {
        RefreshTokenRequest requestBody = new RefreshTokenRequest(REFRESH_TOKEN_GRANT_TYPE, refreshToken);
        return sendRefreshTokenRequest(requestBody);
    }

    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return firebaseAuth.verifyIdToken(idToken, true);
    }

    public UserModel createOrUpdateUserFromFirebase(FirebaseToken decodedToken, String role) {
        String email = decodedToken.getEmail();
        String name = decodedToken.getName() != null ? decodedToken.getName() : "Unknown";
        String firebaseUid = decodedToken.getUid();
        
        UserModel user = userRepository.findByEmail(email);
        
        if (user == null) {
            // Create new user
            user = UserModel.builder()
                    .email(email)
                    .name(name)
                    .firebaseUid(firebaseUid)
                    .roleName(role)
                    .password("") // No password for Firebase users
                    .build();
        } else {
            // Update existing user with Firebase UID
            user.setFirebaseUid(firebaseUid);
            user.setRoleName(role);
        }
        
        return userRepository.save(user);
    }

    public void revokeRefreshTokens(String uid) throws FirebaseAuthException {
        firebaseAuth.revokeRefreshTokens(uid);
    }

    private FirebaseSignInResponse sendSignInRequest(FirebaseSignInRequest firebaseSignInRequest) {
        try {
            return RestClient.create(SIGN_IN_BASE_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(API_KEY_PARAM, firebaseConfig.getWebApiKey())
                            .build())
                    .body(firebaseSignInRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FirebaseSignInResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getResponseBodyAsString().contains(INVALID_CREDENTIALS_ERROR)) {
                throw new RuntimeException("Invalid login credentials provided");
            }
            throw new RuntimeException("Firebase login failed: " + exception.getMessage());
        }
    }

    private RefreshTokenResponse sendRefreshTokenRequest(RefreshTokenRequest refreshTokenRequest) {
        try {
            return RestClient.create(REFRESH_TOKEN_BASE_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(API_KEY_PARAM, firebaseConfig.getWebApiKey())
                            .build())
                    .body(refreshTokenRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(RefreshTokenResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getResponseBodyAsString().contains(INVALID_REFRESH_TOKEN_ERROR)) {
                throw new RuntimeException("Invalid refresh token provided");
            }
            throw new RuntimeException("Token refresh failed: " + exception.getMessage());
        }
    }
}