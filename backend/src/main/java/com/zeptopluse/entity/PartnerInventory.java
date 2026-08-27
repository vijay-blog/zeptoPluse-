package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "partner_inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"partner_code", "product_id"}))
@Getter @Setter @NoArgsConstructor
public class PartnerInventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 80) private String partnerCode;
    @Column(nullable = false, length = 150) private String partnerName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false) private int stockQuantity;
    @Column(precision = 12, scale = 2) private BigDecimal partnerPrice;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist @PreUpdate void updated() { updatedAt = LocalDateTime.now(); }
}
