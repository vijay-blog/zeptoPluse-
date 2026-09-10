package com.daily.nexamartpartner.features.delivery.presentation.location

import android.content.Context
import android.content.Intent
import android.net.Uri

/** External navigation only; the app does not request background location permission. */
object DeliveryNavigationHelper {
    fun openDestination(context: Context, address: String): Boolean {
        val clean = address.trim()
        if (clean.isEmpty()) return false

        val navigation = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("google.navigation:q=${Uri.encode(clean)}")
        ).apply { setPackage("com.google.android.apps.maps") }
        if (navigation.resolveActivity(context.packageManager) != null) {
            context.startActivity(navigation)
            return true
        }

        val mapsSearch = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=${Uri.encode(clean)}")
        )
        if (mapsSearch.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapsSearch)
            return true
        }
        return false
    }

    fun openMapsSearch(context: Context, address: String): Boolean = openDestination(context, address)
}
