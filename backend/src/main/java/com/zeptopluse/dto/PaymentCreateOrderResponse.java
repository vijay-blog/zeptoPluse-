package com.zeptopluse.dto;

import java.math.BigDecimal;

public record PaymentCreateOrderResponse(
        Long paymentId,
        Long orderId,
        String keyId,
        String gateway,
        String gatewayOrderId,
        BigDecimal amount,
        String currency) {}
