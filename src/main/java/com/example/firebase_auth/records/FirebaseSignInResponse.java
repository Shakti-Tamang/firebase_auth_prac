package com.example.firebase_auth.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FirebaseSignInResponse(
    @JsonProperty("idToken") String idToken,
    @JsonProperty("email") String email,
    @JsonProperty("refreshToken") String refreshToken,
    @JsonProperty("expiresIn") String expiresIn,
    @JsonProperty("localId") String localId,
    @JsonProperty("registered") boolean registered
) {}
