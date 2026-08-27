package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
    @Column(nullable = false) private Long productId;
    @Column(nullable = false, length = 64) private String productSku;
    @Column(nullable = false, length = 180) private String productName;
    @Column(length = 100) private String productBrand;
    @Column(length = 500) private String productImageUrl;
    @Column(length = 40) private String productUnit;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal lineTotal;
}
