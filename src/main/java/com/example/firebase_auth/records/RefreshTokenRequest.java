package com.example.firebase_auth.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenRequest(
    @JsonProperty("grant_type") String grantType,
    @JsonProperty("refresh_token") String refreshToken
) {}