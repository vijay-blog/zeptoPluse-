package com.daily.nexamartpartner.features.delivery

import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotification
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliveryNotificationsModelsTest {
    @Test fun unreadNotificationIsRepresentedCorrectly() {
        val n = DeliveryNotification("1", "New order", "Order assigned", null, false, "ORDER", "ORD-1", null)
        assertFalse(n.read)
        assertEquals("ORD-1", n.orderId)
    }

    @Test fun readNotificationCanBeCopiedAsRead() {
        val n = DeliveryNotification("1", "Update", "Done", null, false, null, null, null).copy(read = true)
        assertTrue(n.read)
    }
}
