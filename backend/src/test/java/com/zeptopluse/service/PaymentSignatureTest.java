package com.zeptopluse.service;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentSignatureTest {
    @Test
    void razorpaySignatureFixtureMatchesHmacSha256Contract() throws Exception {
        String signature = hmac("order_test|pay_test", "secret");
        assertThat(signature).isEqualTo("1a671dd53ac846cdfb4f476dda61505d7d79109425d2b8950bf2d524fd846546");
    }

    private String hmac(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
