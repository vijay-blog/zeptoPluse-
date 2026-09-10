package com.zeptopluse.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts")
@Getter @Setter @NoArgsConstructor
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "delivery_partner_id")
    private Long deliveryPartnerId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void created() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void updated() { updatedAt = LocalDateTime.now(); }
}
