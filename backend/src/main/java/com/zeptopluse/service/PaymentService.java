package com.zeptopluse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeptopluse.dto.*;
import com.zeptopluse.entity.*;
import com.zeptopluse.exception.ResourceNotFoundException;
import com.zeptopluse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final OrderRepository orders;
    private final PaymentRepository payments;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Transactional
    public PaymentCreateOrderResponse createOrder(PaymentCreateOrderRequest request) {
        CustomerOrder order = orders.findById(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));
        if (order.getPaymentMethod() != PaymentMethod.ONLINE) throw new IllegalArgumentException("Payment order can be created only for online orders");
        if (order.getPaymentStatus() == PaymentStatus.CAPTURED) throw new IllegalArgumentException("Payment already completed");
        Payment existing = payments.findFirstByOrderIdOrderByCreatedAtDesc(order.getId()).orElse(null);
        if (existing != null && existing.getGatewayOrderId() != null && existing.getStatus() == PaymentStatus.CREATED) return toCreateResponse(existing);
        requireRazorpayConfigured();

        String gatewayOrderId = createRazorpayOrder(order);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setGateway("RAZORPAY");
        payment.setGatewayOrderId(gatewayOrderId);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("INR");
        payment.setStatus(PaymentStatus.CREATED);
        return toCreateResponse(payments.save(payment));
    }

    @Transactional
    public OrderResponse verify(PaymentVerifyRequest request) {
        Payment payment = payments.findByGatewayOrderId(request.gatewayOrderId()).orElseThrow(() -> new ResourceNotFoundException("Payment order not found"));
        if (!payment.getOrder().getId().equals(request.orderId())) throw new IllegalArgumentException("Payment order does not match order");
        if (!validSignature(request.gatewayOrderId(), request.gatewayPaymentId(), request.gatewaySignature())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Invalid Razorpay signature");
            throw new IllegalArgumentException("Payment verification failed");
        }
        payment.setGatewayPaymentId(request.gatewayPaymentId());
        payment.setGatewaySignature(request.gatewaySignature());
        payment.setStatus(PaymentStatus.CAPTURED);
        return orderService.markPaymentCaptured(payment.getOrder().getId());
    }

    public PaymentResponse get(Long id) {
        return toResponse(payments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id)));
    }

    private void requireRazorpayConfigured() {
        if (keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) throw new IllegalStateException("Razorpay credentials are not configured");
    }

    private String createRazorpayOrder(CustomerOrder order) {
        try {
            long amountPaise = order.getTotalAmount().multiply(new BigDecimal("100")).longValueExact();
            Map<String, Object> payload = Map.of(
                    "amount", amountPaise,
                    "currency", "INR",
                    "receipt", order.getOrderNumber(),
                    "payment_capture", 1);
            String body = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1/orders"))
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) throw new IllegalStateException("Razorpay order creation failed");
            JsonNode json = objectMapper.readTree(response.body());
            return json.path("id").asText();
        } catch (Exception e) {
            throw new IllegalStateException("Razorpay order creation failed", e);
        }
    }

    private boolean validSignature(String orderId, String paymentId, String signature) {
        requireRazorpayConfigured();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = HexFormat.of().formatHex(mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8)));
            return MessageDigestSafe.equals(expected, signature);
        } catch (Exception e) {
            throw new IllegalStateException("Payment verification failed", e);
        }
    }

    private PaymentCreateOrderResponse toCreateResponse(Payment payment) {
        return new PaymentCreateOrderResponse(payment.getId(), payment.getOrder().getId(), keyId, payment.getGateway(), payment.getGatewayOrderId(), payment.getAmount(), payment.getCurrency());
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getOrder().getId(), payment.getGateway(), payment.getGatewayOrderId(), payment.getGatewayPaymentId(), payment.getAmount(), payment.getCurrency(), payment.getStatus(), payment.getFailureReason(), payment.getCreatedAt(), payment.getUpdatedAt());
    }

    private static final class MessageDigestSafe {
        static boolean equals(String left, String right) {
            if (left == null || right == null) return false;
            return java.security.MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
        }
    }
}
