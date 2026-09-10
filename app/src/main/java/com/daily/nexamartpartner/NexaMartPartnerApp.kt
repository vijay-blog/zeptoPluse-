package com.daily.nexamartpartner

import android.app.Application
import com.daily.nexamartpartner.core.config.AppConfig
import com.daily.nexamartpartner.di.AppContainer

class NexaMartPartnerApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        AppConfig.initialize()
        appContainer = AppContainer(this)
    }
}
