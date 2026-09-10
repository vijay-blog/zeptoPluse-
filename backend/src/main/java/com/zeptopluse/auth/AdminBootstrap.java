package com.zeptopluse.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;
    private final String name;

    public AdminBootstrap(UserAccountRepository users, PasswordEncoder encoder,
                          @Value("${security.bootstrap-admin.email:}") String email,
                          @Value("${security.bootstrap-admin.password:}") String password,
                          @Value("${security.bootstrap-admin.name:NexaMart Admin}") String name) {
        this.users = users; this.encoder = encoder; this.email = email; this.password = password; this.name = name;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) return;
        if (password.length() < 8) throw new IllegalStateException("security.bootstrap-admin.password must contain at least 8 characters");
        String normalized = email.trim().toLowerCase(java.util.Locale.ROOT);
        if (users.existsByEmailIgnoreCase(normalized)) return;
        UserAccount admin = new UserAccount();
        admin.setName(name.trim().isBlank() ? "NexaMart Admin" : name.trim());
        admin.setEmail(normalized);
        admin.setPasswordHash(encoder.encode(password));
        admin.setRole(AccountRole.ADMIN);
        admin.setStatus(AccountStatus.ACTIVE);
        users.save(admin);
    }
}
