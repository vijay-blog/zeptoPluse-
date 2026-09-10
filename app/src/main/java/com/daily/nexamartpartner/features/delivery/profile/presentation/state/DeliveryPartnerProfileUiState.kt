package com.daily.nexamartpartner.features.delivery.profile.presentation.state
import com.daily.nexamartpartner.features.delivery.profile.domain.model.DeliveryPartnerProfile

data class DeliveryPartnerProfileUiState(val loading:Boolean=true,val saving:Boolean=false,val profile:DeliveryPartnerProfile?=null,val unavailable:Boolean=false,val error:String?=null)
