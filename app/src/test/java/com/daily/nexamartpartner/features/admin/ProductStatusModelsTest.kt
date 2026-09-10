package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.features.admin.domain.model.ProductAvailability
import com.daily.nexamartpartner.features.admin.domain.model.ProductStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductStatusModelsTest {
    @Test
    fun `backend product statuses map centrally`() {
        assertEquals(ProductStatus.ACTIVE, ProductStatus.fromRaw("ACTIVE"))
        assertEquals(ProductStatus.INACTIVE, ProductStatus.fromRaw("INACTIVE"))
        assertEquals(ProductStatus.DRAFT, ProductStatus.fromRaw("DRAFT"))
        assertEquals(ProductStatus.OUT_OF_STOCK, ProductStatus.fromRaw("OUT_OF_STOCK"))
        assertEquals(ProductStatus.UNKNOWN, ProductStatus.fromRaw("unsupported"))
        assertEquals(ProductStatus.UNKNOWN, ProductStatus.fromRaw(null))
    }

    @Test
    fun `backend product availability maps centrally`() {
        assertEquals(ProductAvailability.IN_STOCK, ProductAvailability.fromRaw("IN_STOCK"))
        assertEquals(ProductAvailability.LOW_STOCK, ProductAvailability.fromRaw("LOW_STOCK"))
        assertEquals(ProductAvailability.OUT_OF_STOCK, ProductAvailability.fromRaw("OUT_OF_STOCK"))
        assertEquals(ProductAvailability.UNKNOWN, ProductAvailability.fromRaw("unsupported"))
    }
}
