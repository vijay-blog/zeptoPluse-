package com.daily.nexamartpartner.features.delivery.profile.domain.usecase
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.profile.domain.model.*
import com.daily.nexamartpartner.features.delivery.profile.domain.repository.DeliveryPartnerProfileRepository
class GetDeliveryPartnerProfileUseCase(private val repo:DeliveryPartnerProfileRepository){suspend operator fun invoke()=repo.getProfile()}
class UpdateDeliveryPartnerProfileUseCase(private val repo:DeliveryPartnerProfileRepository){suspend operator fun invoke(update:DeliveryPartnerProfileUpdate)=repo.updateProfile(update)}
