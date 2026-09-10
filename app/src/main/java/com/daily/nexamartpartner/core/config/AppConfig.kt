package com.daily.nexamartpartner.core.config

import com.daily.nexamartpartner.BuildConfig

object AppConfig {
    lateinit var environment: AppEnvironment
        private set

    lateinit var baseUrl: String
        private set

    var enableNetworkLogging: Boolean = false
        private set

    fun initialize() {
        environment = AppEnvironment.fromRaw(BuildConfig.APP_ENV)
        baseUrl = BuildConfig.BASE_URL
        enableNetworkLogging = BuildConfig.ENABLE_NETWORK_LOGGING
    }
}
