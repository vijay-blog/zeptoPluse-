package com.zeptopluse.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentCreateOrderRequest(@NotNull Long orderId) {}
