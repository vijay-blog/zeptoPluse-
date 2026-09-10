package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partners")
@Getter @Setter @NoArgsConstructor
public class DeliveryPartner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false, unique = true, length = 20) private String phone;
    @Column(unique = true, length = 160) private String email;
    @Column(length = 40) private String vehicleNumber;
    @Column(length = 40) private String vehicleType;
    @Column(length = 120) private String licenseReference;
    @Column(nullable = false, length = 30) private String verificationStatus = "VERIFIED";
    @Column(nullable = false) private boolean active = true;
    @Column(nullable = false) private boolean available = true;
    private LocalDateTime lastActiveAt;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @PrePersist void created() { createdAt = LocalDateTime.now(); lastActiveAt = createdAt; }
}
