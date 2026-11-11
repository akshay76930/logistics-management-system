package com.logistics.dashboard.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
@Profile("!local")
public class AuthService {

    private final String username;
    private final String passwordHash;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            @Value("${app.auth.username}") String username,
            @Value("${app.auth.password-hash}") String passwordHash,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        boolean correctUsername = MessageDigest.isEqual(
                username.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                request.username().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (!correctUsername || !passwordEncoder.matches(request.password(), passwordHash)) {
            throw new InvalidCredentialsException();
        }
        return new LoginResponse(jwtService.createToken(username), "Bearer", jwtService.getExpirationSeconds());
    }
}
