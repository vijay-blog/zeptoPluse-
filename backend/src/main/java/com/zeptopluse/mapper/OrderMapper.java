package com.zeptopluse.mapper;

import com.zeptopluse.dto.*;
import com.zeptopluse.entity.*;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponse toResponse(CustomerOrder order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getCustomer().getId(), order.getStatus(), order.getPaymentMethod(), order.getPaymentStatus(), order.getSubtotal(), order.getDeliveryFee(), order.getDiscountAmount(), order.getTotalAmount(), order.getAddressSnapshot(), order.getCreatedAt(), order.getUpdatedAt(), order.getItems().stream().map(item -> new OrderItemResponse(item.getProductId(), item.getProductSku(), item.getProductName(), item.getProductBrand(), item.getProductImageUrl(), item.getProductUnit(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal())).toList());
    }
}
