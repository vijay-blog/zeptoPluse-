package com.daily.nexamartpartner.features.admin.customer.data.contract

import com.daily.nexamartpartner.features.admin.customer.domain.model.CustomerAdminAction
import com.daily.nexamartpartner.features.admin.customer.domain.model.CustomerQuery

interface CustomerManagementContract {
    val listPath: String?
    val detailsPathTemplate: String?
    val actionPathTemplate: String?
    fun buildListQuery(query: CustomerQuery): Map<String, String>?
    fun buildActionBody(action: CustomerAdminAction): Map<String, String>?
    fun resolvePath(template: String?, id: String): String?
}

class PendingBackendCustomerManagementContract : CustomerManagementContract {
    override val listPath: String? = null
    override val detailsPathTemplate: String? = null
    override val actionPathTemplate: String? = null
    override fun buildListQuery(query: CustomerQuery): Map<String, String>? = null
    override fun buildActionBody(action: CustomerAdminAction): Map<String, String>? = null
    override fun resolvePath(template: String?, id: String): String? = null
}
