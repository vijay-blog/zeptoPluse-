package com.daily.nexamartpartner.features.admin.category.data.model

import com.squareup.moshi.Json

data class CategoryDto(
    @Json(name="categoryId") val categoryId: String?, @Json(name="name") val name: String?,
    @Json(name="description") val description: String?, @Json(name="imageUrl") val imageUrl: String?,
    @Json(name="active") val active: Boolean?, @Json(name="productCount") val productCount: Int?,
    @Json(name="sortOrder") val sortOrder: Int?, @Json(name="createdAt") val createdAt: String?,
    @Json(name="updatedAt") val updatedAt: String?, @Json(name="allowedActions") val allowedActions: List<String>?
)
data class CategoriesPageDto(@Json(name="content") val content: List<CategoryDto>?, @Json(name="page") val page: Int?, @Json(name="pageSize") val pageSize: Int?, @Json(name="totalPages") val totalPages: Int?, @Json(name="totalElements") val totalElements: Long?, @Json(name="hasNextPage") val hasNextPage: Boolean?)
