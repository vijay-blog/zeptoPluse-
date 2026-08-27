package com.zeptopluse.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record AddressRequest(
        @NotBlank @Size(max = 30) String label,
        @NotBlank @Size(max = 120) @JsonAlias("fullName") String recipientName,
        @NotBlank @Pattern(regexp = "^[0-9+ -]{7,20}$") @JsonAlias("mobile") String phone,
        @NotBlank @Size(max = 250) @JsonAlias("house") String line1,
        @Size(max = 250) @JsonAlias("street") String line2,
        @Size(max = 150) @JsonAlias("area") String landmark,
        @NotBlank @Size(max = 80) String city,
        @NotBlank @Size(max = 80) String state,
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9 -]{4,12}$") @JsonAlias("pincode") String postalCode,
        @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
        @JsonAlias("isDefault") boolean defaultAddress) {}
