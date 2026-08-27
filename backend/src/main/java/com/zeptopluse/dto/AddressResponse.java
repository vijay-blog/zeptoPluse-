package com.zeptopluse.dto;

import java.math.BigDecimal;
public record AddressResponse(Long id, Long customerId, String label, String recipientName, String phone, String line1, String line2, String landmark, String city, String state, String postalCode, BigDecimal latitude, BigDecimal longitude, boolean defaultAddress) {}
