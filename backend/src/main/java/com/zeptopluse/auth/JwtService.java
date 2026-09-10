package com.zeptopluse.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final byte[] secret;
    private final long accessSeconds;
    private final ObjectMapper mapper;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-expiration-seconds:3600}") long accessSeconds,
            ObjectMapper mapper) {
        if (secret == null || secret.length() < 32 || secret.startsWith("CHANGE_ME_")) {
            throw new IllegalStateException("security.jwt.secret must be at least 32 characters");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.accessSeconds = accessSeconds;
        this.mapper = mapper;
    }

    public long accessExpirationSeconds() { return accessSeconds; }

    public String createAccessToken(UserAccount user) {
        long now = Instant.now().getEpochSecond();
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encodeJson(Map.of(
                "sub", user.getId().toString(),
                "role", user.getRole().name(),
                "email", user.getEmail(),
                "iat", now,
                "exp", now + accessSeconds,
                "jti", UUID.randomUUID().toString()));
        String signingInput = header + "." + payload;
        return signingInput + "." + sign(signingInput);
    }

    public Claims parseAndValidate(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3 || !constantTimeEquals(parts[2], sign(parts[0] + "." + parts[1]))) return null;
            Map<?, ?> claims = mapper.readValue(new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8), Map.class);
            long exp = ((Number) claims.get("exp")).longValue();
            if (Instant.now().getEpochSecond() >= exp) return null;
            return new Claims(Long.parseLong(String.valueOf(claims.get("sub"))), String.valueOf(claims.get("role")), String.valueOf(claims.get("email")));
        } catch (Exception ignored) { return null; }
    }

    private String encode(String value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    private String encodeJson(Object value) { try { return encode(mapper.writeValueAsString(value)); } catch (Exception e) { throw new IllegalStateException("Unable to create access token", e); } }
    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException("Unable to sign access token", e); }
    }
    private boolean constantTimeEquals(String a, String b) { return java.security.MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8)); }

    public record Claims(Long userId, String role, String email) {}
}
