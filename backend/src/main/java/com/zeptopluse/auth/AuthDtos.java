package com.zeptopluse.auth;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Email @Size(max = 160) String email,
            @Size(max = 20) String phone,
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record LoginRequest(
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank String password) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}

    public record AuthUser(Long id, String name, String phone, String email, String role) {}

    public record AuthResponse(String accessToken, String refreshToken, long expiresInSeconds, AuthUser user) {}
}
