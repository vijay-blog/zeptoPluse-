package com.zeptopluse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Field names retain Flutter's existing product contract while exposing the canonical catalogue data. */
public record ProductResponse(
        Long id, String sku, String name, String description, String brand,
        BigDecimal mrp, BigDecimal sellingPrice, BigDecimal discountPercentage,
        String unit, String weight, String size, String imageUrl, String imageAsset,
        boolean availability, boolean available, int stockQuantity, String deliveryType,
        Long categoryId, String categoryName, LocalDateTime createdAt, LocalDateTime updatedAt) {}
