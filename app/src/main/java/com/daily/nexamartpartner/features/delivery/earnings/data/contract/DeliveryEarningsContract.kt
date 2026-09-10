package com.daily.nexamartpartner.features.delivery.earnings.data.contract

import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsQuery

interface DeliveryEarningsContract {
    val summaryPath: String?
    val historyPath: String?
    fun buildSummaryQuery(query: DeliveryEarningsQuery): Map<String, String>?
    fun buildHistoryQuery(query: DeliveryEarningsQuery): Map<String, String>?
}

class PendingBackendDeliveryEarningsContract : DeliveryEarningsContract {
    override val summaryPath: String? = null
    override val historyPath: String? = null
    override fun buildSummaryQuery(query: DeliveryEarningsQuery): Map<String, String>? = null
    override fun buildHistoryQuery(query: DeliveryEarningsQuery): Map<String, String>? = null
}
