package com.example.firebase_auth.records;

public record FirebaseSignInRequest(
    String email, 
    String password, 
    boolean returnSecureToken
) {}
