package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "addresses")
@Getter @Setter @NoArgsConstructor
public class CustomerAddress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @Column(nullable = false, length = 30) private String label;
    @Column(nullable = false, length = 120) private String recipientName;
    @Column(nullable = false, length = 20) private String phone;
    @Column(nullable = false, length = 250) private String line1;
    @Column(length = 250) private String line2;
    @Column(length = 150) private String landmark;
    @Column(nullable = false, length = 80) private String city;
    @Column(nullable = false, length = 80) private String state;
    @Column(nullable = false, length = 12) private String postalCode;
    @Column(precision = 10, scale = 7) private BigDecimal latitude;
    @Column(precision = 10, scale = 7) private BigDecimal longitude;
    @Column(nullable = false) private boolean defaultAddress;
}
