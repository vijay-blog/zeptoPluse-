package com.daily.nexamartpartner.features.admin.category.domain.repository
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.category.domain.model.*
interface CategoryManagementRepository { suspend fun list(q:CategoryQuery):AppResult<PagedCategories>; suspend fun details(id:String):AppResult<Category>; suspend fun create(d:CategoryDraft):AppResult<Category>; suspend fun update(id:String,d:CategoryDraft):AppResult<Category>; suspend fun action(id:String,a:CategoryAdminAction):AppResult<Unit> }
