package com.zeptopluse.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;

/** A guest may omit a display name; the service assigns a safe guest default. */
public record CustomerRequest(
        @Size(max = 120) String name,
        @NotBlank @Pattern(regexp = "^[0-9+ -]{7,20}$") @JsonAlias("mobile") String phone,
        @Email @Size(max = 160) String email) {}
