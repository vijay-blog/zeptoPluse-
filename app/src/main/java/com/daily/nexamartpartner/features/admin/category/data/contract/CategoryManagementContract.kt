package com.daily.nexamartpartner.features.admin.category.data.contract

import com.daily.nexamartpartner.features.admin.category.domain.model.*

interface CategoryManagementContract {
    val listPath: String?; val detailsPathTemplate: String?; val createPath: String?; val updatePathTemplate: String?; val actionPathTemplate: String?
    fun buildListQuery(query: CategoryQuery): Map<String,String>?
    fun buildDraftBody(draft: CategoryDraft): Map<String,String>?
    fun buildActionBody(action: CategoryAdminAction): Map<String,String>?
    fun resolvePath(template: String?, id: String): String?
}
class PendingBackendCategoryManagementContract : CategoryManagementContract {
    override val listPath: String? = null; override val detailsPathTemplate: String? = null; override val createPath: String? = null; override val updatePathTemplate: String? = null; override val actionPathTemplate: String? = null
    override fun buildListQuery(query: CategoryQuery) = null
    override fun buildDraftBody(draft: CategoryDraft) = null
    override fun buildActionBody(action: CategoryAdminAction) = null
    override fun resolvePath(template: String?, id: String) = null
}
