package com.daily.nexamartpartner.di

import android.content.Context
import com.daily.nexamartpartner.NexaMartPartnerApp

val Context.appContainer: AppContainer
    get() = (applicationContext as NexaMartPartnerApp).appContainer
