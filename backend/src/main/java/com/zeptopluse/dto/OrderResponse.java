package com.zeptopluse.dto;

import com.zeptopluse.entity.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String orderNumber, Long customerId, OrderStatus status, PaymentMethod paymentMethod, PaymentStatus paymentStatus, BigDecimal subtotal, BigDecimal deliveryFee, BigDecimal discountAmount, BigDecimal totalAmount, String addressSnapshot, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItemResponse> items) {}
