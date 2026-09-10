package com.zeptopluse.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private final JwtService service = new JwtService("01234567890123456789012345678901", 3600, new ObjectMapper());
    @Test void createsAndValidatesAccessToken() { UserAccount user=new UserAccount(); user.setId(42L); user.setEmail("admin@example.com"); user.setRole(AccountRole.ADMIN); String token=service.createAccessToken(user); JwtService.Claims claims=service.parseAndValidate(token); assertNotNull(claims); assertEquals(42L,claims.userId()); assertEquals("ADMIN",claims.role()); }
    @Test void rejectsTamperedToken() { UserAccount user=new UserAccount(); user.setId(42L); user.setEmail("admin@example.com"); user.setRole(AccountRole.ADMIN); String token=service.createAccessToken(user); assertNull(service.parseAndValidate(token+"x")); }
}
