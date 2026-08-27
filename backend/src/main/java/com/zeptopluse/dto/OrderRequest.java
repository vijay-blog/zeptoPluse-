package com.zeptopluse.dto;

import com.zeptopluse.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record OrderRequest(
        @NotNull Long customerId,
        Long addressId,
        @Valid AddressRequest address,
        PaymentMethod paymentMethod,
        @Size(max = 80) String idempotencyKey,
        @NotEmpty @Size(max = 30) List<@Valid OrderItemRequest> items) {}