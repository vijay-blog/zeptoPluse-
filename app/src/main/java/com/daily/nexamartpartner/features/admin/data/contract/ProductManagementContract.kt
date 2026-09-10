package com.daily.nexamartpartner.features.admin.data.contract

import com.daily.nexamartpartner.features.admin.domain.model.ProductAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.ProductDraft
import com.daily.nexamartpartner.features.admin.domain.model.ProductsQuery

/**
 * Isolates every backend-dependent path/query-key/body-key decision for product management.
 * No Java/Spring backend source exists in this repository, so this contract cannot be
 * confirmed here. [PendingBackendProductManagementContract] is the only implementation and
 * intentionally returns null everywhere so no guessed request is ever sent.
 */
interface ProductManagementContract {
    val listProductsPath: String?
    val productDetailsPathTemplate: String?
    val categoryOptionsPath: String?
    val createProductPath: String?
    val updateProductPathTemplate: String?
    val productActionPathTemplate: String?

    fun buildProductListQuery(query: ProductsQuery): Map<String, String>?
    fun buildCreateProductBody(draft: ProductDraft): Map<String, String>?
    fun buildUpdateProductBody(draft: ProductDraft): Map<String, String>?
    fun buildProductActionBody(action: ProductAdminAction): Map<String, String>?
    fun resolvePath(template: String?, productId: String): String?
}

class PendingBackendProductManagementContract : ProductManagementContract {
    override val listProductsPath: String? = null
    override val productDetailsPathTemplate: String? = null
    override val categoryOptionsPath: String? = null
    override val createProductPath: String? = null
    override val updateProductPathTemplate: String? = null
    override val productActionPathTemplate: String? = null

    override fun buildProductListQuery(query: ProductsQuery): Map<String, String>? = null
    override fun buildCreateProductBody(draft: ProductDraft): Map<String, String>? = null
    override fun buildUpdateProductBody(draft: ProductDraft): Map<String, String>? = null
    override fun buildProductActionBody(action: ProductAdminAction): Map<String, String>? = null
    override fun resolvePath(template: String?, productId: String): String? = null
}
