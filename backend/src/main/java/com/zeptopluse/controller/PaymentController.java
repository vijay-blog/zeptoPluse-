package com.zeptopluse.controller;

import com.zeptopluse.dto.*;
import com.zeptopluse.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping("/create-order")
    public PaymentCreateOrderResponse createOrder(@Valid @RequestBody PaymentCreateOrderRequest request) {
        return service.createOrder(request);
    }

    @PostMapping("/verify")
    public OrderResponse verify(@Valid @RequestBody PaymentVerifyRequest request) {
        return service.verify(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable Long id) {
        return service.get(id);
    }
}
