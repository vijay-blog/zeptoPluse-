package com.daily.nexamartpartner.features.admin.category.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.admin.category.data.contract.CategoryManagementContract
import com.daily.nexamartpartner.features.admin.category.data.model.*
import com.daily.nexamartpartner.features.admin.category.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface CategoryManagementApi {
 @GET suspend fun list(@Url path:String,@QueryMap params:Map<String,String>):Response<CategoriesPageDto>
 @GET suspend fun details(@Url path:String):Response<CategoryDto>
 @POST suspend fun create(@Url path:String,@Body body:Map<String,String>):Response<CategoryDto>
 @PATCH suspend fun update(@Url path:String,@Body body:Map<String,String>):Response<CategoryDto>
 @PATCH suspend fun action(@Url path:String,@Body body:Map<String,String>):Response<Unit>
}
interface CategoryManagementRemoteDataSource { suspend fun list(q:CategoryQuery):AppResult<CategoriesPageDto>; suspend fun details(id:String):AppResult<CategoryDto>; suspend fun create(d:CategoryDraft):AppResult<CategoryDto>; suspend fun update(id:String,d:CategoryDraft):AppResult<CategoryDto>; suspend fun action(id:String,a:CategoryAdminAction):AppResult<Unit> }
class CategoryManagementRemoteDataSourceImpl(private val api:CategoryManagementApi,private val contract:CategoryManagementContract,private val executor:ApiCallExecutor):CategoryManagementRemoteDataSource {
 private fun <T> missing(m:String):AppResult<T> = AppResult.Failure(AppFailure(m,type=FailureType.CONTRACT_MISSING))
 override suspend fun list(q:CategoryQuery):AppResult<CategoriesPageDto>{val p=contract.listPath?:return missing("Category list API contract is not confirmed yet.");val params=contract.buildListQuery(q)?:return missing("Category list query contract is not confirmed yet.");return executor.execute{api.list(p,params)}}
 override suspend fun details(id:String)=contract.resolvePath(contract.detailsPathTemplate,id)?.let{p->executor.execute{api.details(p)}}?:missing("Category details API contract is not confirmed yet.")
 override suspend fun create(d:CategoryDraft):AppResult<CategoryDto>{val p=contract.createPath?:return missing("Category creation API contract is not confirmed yet.");val b=contract.buildDraftBody(d)?:return missing("Category creation request contract is not confirmed yet.");return executor.execute{api.create(p,b)}}
 override suspend fun update(id:String,d:CategoryDraft):AppResult<CategoryDto>{val p=contract.resolvePath(contract.updatePathTemplate,id)?:return missing("Category update API contract is not confirmed yet.");val b=contract.buildDraftBody(d)?:return missing("Category update request contract is not confirmed yet.");return executor.execute{api.update(p,b)}}
 override suspend fun action(id:String,a:CategoryAdminAction):AppResult<Unit>{val p=contract.resolvePath(contract.actionPathTemplate,id)?:return missing("Category action API contract is not confirmed yet.");val b=contract.buildActionBody(a)?:return missing("Category action request contract is not confirmed yet.");return when(val r=executor.execute{api.action(p,b)}){is AppResult.Success->AppResult.Success(Unit);is AppResult.Failure->r}}
}
