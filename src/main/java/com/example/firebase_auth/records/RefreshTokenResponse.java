package com.example.firebase_auth.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenResponse(
    @JsonProperty("id_token") String idToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") String expiresIn,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("user_id") String userId,
    @JsonProperty("project_id") String projectId
) {}