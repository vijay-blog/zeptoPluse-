package com.zeptopluse.repository;

import com.zeptopluse.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
    Optional<Payment> findFirstByOrderIdOrderByCreatedAtDesc(Long orderId);
}
