package com.zeptopluse.dto;

import com.zeptopluse.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        String gateway,
        String gatewayOrderId,
        String gatewayPaymentId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
