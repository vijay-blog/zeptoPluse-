package com.daily.nexamartpartner.features.admin.category.data.repository
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.admin.category.data.model.*
import com.daily.nexamartpartner.features.admin.category.data.source.CategoryManagementRemoteDataSource
import com.daily.nexamartpartner.features.admin.category.domain.model.*
import com.daily.nexamartpartner.features.admin.category.domain.repository.CategoryManagementRepository
class CategoryManagementRepositoryImpl(private val remote:CategoryManagementRemoteDataSource):CategoryManagementRepository{
 override suspend fun list(q:CategoryQuery)=when(val r=remote.list(q)){is AppResult.Success->AppResult.Success(r.data.toDomain());is AppResult.Failure->r}
 override suspend fun details(id:String)=when(val r=remote.details(id)){is AppResult.Success->map(r.data);is AppResult.Failure->r}
 override suspend fun create(d:CategoryDraft)=when(val r=remote.create(d)){is AppResult.Success->map(r.data);is AppResult.Failure->r}
 override suspend fun update(id:String,d:CategoryDraft)=when(val r=remote.update(id,d)){is AppResult.Success->map(r.data);is AppResult.Failure->r}
 override suspend fun action(id:String,a:CategoryAdminAction)=remote.action(id,a)
 private fun map(d:CategoryDto):AppResult<Category>{val id=d.categoryId?.trim().orEmpty();if(id.isBlank())return AppResult.Failure(AppFailure("Category response is missing categoryId.",type=FailureType.UNKNOWN));return AppResult.Success(Category(id,d.name?.trim().orEmpty(),d.description,d.imageUrl,d.active,d.productCount,d.sortOrder,d.createdAt,d.updatedAt,d.allowedActions.orEmpty().mapNotNull{x->CategoryAdminAction.entries.firstOrNull{it.backendValue.equals(x,ignoreCase=true)}}))}
 private fun CategoriesPageDto.toDomain():PagedCategories=PagedCategories(content.orEmpty().mapNotNull{d->d.categoryId?.takeIf{it.isNotBlank()}?.let{id->Category(id,d.name?.trim().orEmpty(),d.description,d.imageUrl,d.active,d.productCount,d.sortOrder,d.createdAt,d.updatedAt,d.allowedActions.orEmpty().mapNotNull{x->CategoryAdminAction.entries.firstOrNull{it.backendValue.equals(x,ignoreCase=true)}})}},page?:0,pageSize?:20,totalPages?:0,totalElements?:0L,hasNextPage?:false)
}
