package com.daily.nexamartpartner.features.admin.category.domain.usecase
import com.daily.nexamartpartner.features.admin.category.domain.model.*
import com.daily.nexamartpartner.features.admin.category.domain.repository.CategoryManagementRepository
class GetCategoriesUseCase(private val r:CategoryManagementRepository){suspend operator fun invoke(q:CategoryQuery)=r.list(q)}
class GetCategoryDetailsUseCase(private val r:CategoryManagementRepository){suspend operator fun invoke(id:String)=r.details(id)}
class CreateCategoryUseCase(private val r:CategoryManagementRepository){suspend operator fun invoke(d:CategoryDraft)=r.create(d)}
class UpdateCategoryUseCase(private val r:CategoryManagementRepository){suspend operator fun invoke(id:String,d:CategoryDraft)=r.update(id,d)}
class PerformCategoryActionUseCase(private val r:CategoryManagementRepository){suspend operator fun invoke(id:String,a:CategoryAdminAction)=r.action(id,a)}
