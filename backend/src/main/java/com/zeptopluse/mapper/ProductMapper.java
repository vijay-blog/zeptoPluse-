package com.zeptopluse.mapper;

import com.zeptopluse.dto.ProductResponse;
import com.zeptopluse.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getSku(), product.getName(), product.getDescription(), product.getBrand(), product.getMrp(), product.getSellingPrice(), product.getDiscountPercentage(), product.getUnit(), product.getWeight(), product.getSize(), product.getImageUrl(), product.getImageUrl(), product.isAvailability(), product.isAvailability(), product.getStockQuantity(), product.getDeliveryType(), product.getCategory().getId(), product.getCategory().getName(), product.getCreatedAt(), product.getUpdatedAt());
    }
}
