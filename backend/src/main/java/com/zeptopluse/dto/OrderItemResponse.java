package com.zeptopluse.dto;

import java.math.BigDecimal;
public record OrderItemResponse(Long productId, String productSku, String productName, String productBrand, String productImageUrl, String productUnit, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
