package com.daily.nexamartpartner.features.admin.customer.domain.usecase

import com.daily.nexamartpartner.features.admin.customer.domain.model.*
import com.daily.nexamartpartner.features.admin.customer.domain.repository.CustomerManagementRepository

class GetCustomersUseCase(private val repo: CustomerManagementRepository) { suspend operator fun invoke(query: CustomerQuery) = repo.list(query) }
class GetCustomerDetailsUseCase(private val repo: CustomerManagementRepository) { suspend operator fun invoke(id: String) = repo.details(id) }
class PerformCustomerAdminActionUseCase(private val repo: CustomerManagementRepository) { suspend operator fun invoke(id: String, action: CustomerAdminAction) = repo.action(id, action) }
