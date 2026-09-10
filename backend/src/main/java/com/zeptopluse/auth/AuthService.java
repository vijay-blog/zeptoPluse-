package com.zeptopluse.auth;

import com.zeptopluse.exception.ConflictException;
import com.zeptopluse.entity.DeliveryPartner;
import com.zeptopluse.repository.DeliveryPartnerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DeliveryPartnerRepository deliveryPartners;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmailIgnoreCase(email)) throw new ConflictException("An account with this email already exists");
        UserAccount user = new UserAccount();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(AccountRole.DELIVERY_PARTNER);
        user.setStatus(AccountStatus.ACTIVE);
        DeliveryPartner partner = new DeliveryPartner();
        partner.setName(user.getName());
        partner.setEmail(email);
        partner.setPhone(normalizePhone(request.phone(), email));
        partner.setVerificationStatus("VERIFIED");
        partner.setActive(true);
        partner.setAvailable(true);
        DeliveryPartner savedPartner = deliveryPartners.save(partner);
        user.setDeliveryPartnerId(savedPartner.getId());
        UserAccount saved = users.save(user);
        return issueTokens(saved);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        UserAccount user = users.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (user.getStatus() != AccountStatus.ACTIVE || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthDtos.AuthResponse refresh(String rawToken) {
        RefreshToken token = refreshTokens.findByTokenHashAndRevokedFalse(hash(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getUser().getStatus() != AccountStatus.ACTIVE) {
            token.setRevoked(true); refreshTokens.save(token);
            throw new IllegalArgumentException("Refresh token expired");
        }
        token.setRevoked(true);
        refreshTokens.save(token);
        return issueTokens(token.getUser());
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokens.findByTokenHashAndRevokedFalse(hash(rawToken)).ifPresent(token -> { token.setRevoked(true); refreshTokens.save(token); });
    }

    private AuthDtos.AuthResponse issueTokens(UserAccount user) {
        String refresh = newRefreshToken();
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hash(refresh));
        entity.setExpiresAt(LocalDateTime.now().plusDays(30));
        refreshTokens.save(entity);
        String phone = user.getDeliveryPartnerId() == null ? null : deliveryPartners.findById(user.getDeliveryPartnerId()).map(DeliveryPartner::getPhone).orElse(null);
        AuthDtos.AuthUser authUser = new AuthDtos.AuthUser(user.getId(), user.getName(), phone, user.getEmail(), user.getRole().name());
        return new AuthDtos.AuthResponse(jwtService.createAccessToken(user), refresh, jwtService.accessExpirationSeconds(), authUser);
    }

    private String newRefreshToken() {
        byte[] bytes = new byte[48]; secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private String normalizePhone(String phone, String email) {
        if (phone != null && !phone.isBlank()) return phone.trim();
        int hash = Math.abs(email.hashCode());
        return String.format("900%07d", hash % 10000000);
    }
    private String normalizeEmail(String email) { return email.trim().toLowerCase(java.util.Locale.ROOT); }
    private String hash(String value) { try { return Base64.getUrlEncoder().withoutPadding().encodeToString(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); } catch (Exception e) { throw new IllegalStateException(e); } }
}
