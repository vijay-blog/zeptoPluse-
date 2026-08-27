package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Version private Long version;
    @Column(nullable = false, unique = true, length = 64) private String sku;
    @Column(nullable = false, length = 180) private String name;
    @Column(length = 2000) private String description;
    @Column(length = 100) private String brand;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal mrp;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal sellingPrice;
    @Column(nullable = false, precision = 5, scale = 2) private BigDecimal discountPercentage;
    @Column(nullable = false, length = 40) private String unit;
    @Column(length = 40) private String weight;
    @Column(length = 40) private String size;
    @Column(length = 500) private String imageUrl;
    @Column(nullable = false) private boolean availability = true;
    @Column(nullable = false) private int stockQuantity;
    @Column(length = 50) private String deliveryType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id", nullable = false) private Category category;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist void created() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void updated() { updatedAt = LocalDateTime.now(); }
}
