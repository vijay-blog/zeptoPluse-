package com.daily.nexamartpartner.features.delivery.profile.domain.repository
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.profile.domain.model.*
interface DeliveryPartnerProfileRepository { suspend fun getProfile():AppResult<DeliveryPartnerProfile>; suspend fun updateProfile(update:DeliveryPartnerProfileUpdate):AppResult<DeliveryPartnerProfile> }
