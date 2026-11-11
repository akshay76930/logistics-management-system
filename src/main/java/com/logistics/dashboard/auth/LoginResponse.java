package com.logistics.dashboard.auth;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
