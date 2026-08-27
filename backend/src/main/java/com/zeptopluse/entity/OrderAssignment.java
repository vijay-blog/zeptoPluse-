package com.zeptopluse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_assignments")
@Getter @Setter @NoArgsConstructor
public class OrderAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "delivery_partner_id", nullable = false) private DeliveryPartner deliveryPartner;
    @Column(nullable = false, length = 30) private String status;
    @Column(nullable = false, updatable = false) private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    @PrePersist void assigned() { assignedAt = LocalDateTime.now(); }
}
