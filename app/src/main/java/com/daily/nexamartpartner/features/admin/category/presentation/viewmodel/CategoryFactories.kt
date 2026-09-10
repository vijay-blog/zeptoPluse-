package com.daily.nexamartpartner.features.admin.category.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.admin.category.domain.usecase.*
class CategoryListViewModelFactory(private val useCase:GetCategoriesUseCase):ViewModelProvider.Factory{override fun <T:ViewModel>create(modelClass:Class<T>):T=CategoryListViewModel(useCase) as T}
class CategoryDetailsViewModelFactory(private val id:String,private val get:GetCategoryDetailsUseCase,private val action:PerformCategoryActionUseCase):ViewModelProvider.Factory{override fun <T:ViewModel>create(modelClass:Class<T>):T=CategoryDetailsViewModel(id,get,action) as T}
class CategoryFormViewModelFactory(private val id:String?,private val get:GetCategoryDetailsUseCase,private val create:CreateCategoryUseCase,private val update:UpdateCategoryUseCase):ViewModelProvider.Factory{override fun <T:ViewModel>create(modelClass:Class<T>):T=CategoryFormViewModel(id,get,create,update) as T}
