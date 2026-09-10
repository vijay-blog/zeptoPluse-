package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.PaymentMethod
import com.daily.nexamartpartner.features.admin.domain.model.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class OrderStatusModelsTest {
    @Test
    fun `order status maps from backend values`() {
        assertEquals(OrderStatus.PENDING, OrderStatus.fromRaw("PENDING"))
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.fromRaw("OUT_FOR_DELIVERY"))
        assertEquals(OrderStatus.UNKNOWN, OrderStatus.fromRaw("NOT_A_STATUS"))
    }

    @Test
    fun `payment status maps from backend values`() {
        assertEquals(PaymentStatus.PAID, PaymentStatus.fromRaw("PAID"))
        assertEquals(PaymentStatus.UNKNOWN, PaymentStatus.fromRaw("X"))
    }

    @Test
    fun `payment method maps from backend values`() {
        assertEquals(PaymentMethod.COD, PaymentMethod.fromRaw("COD"))
        assertEquals(PaymentMethod.UNKNOWN, PaymentMethod.fromRaw("WALLET"))
    }
}
