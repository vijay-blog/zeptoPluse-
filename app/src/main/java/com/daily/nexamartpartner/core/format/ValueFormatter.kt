package com.daily.nexamartpartner.core.format

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object ValueFormatter {
    private val indiaLocale = Locale.forLanguageTag("en-IN")

    fun formatCount(value: Long): String {
        return NumberFormat.getNumberInstance(indiaLocale).format(value)
    }

    fun formatCurrency(amount: BigDecimal, currencyCode: String?): String {
        val formatter = NumberFormat.getCurrencyInstance(indiaLocale)
        if (!currencyCode.isNullOrBlank()) {
            runCatching { Currency.getInstance(currencyCode) }
                .getOrNull()
                ?.let { formatter.currency = it }
        }
        return formatter.format(amount)
    }
}
