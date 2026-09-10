package com.daily.nexamartpartner.core.widgets

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

object UiFeedback {
    fun showSnackbar(anchor: View, message: String) {
        Snackbar.make(anchor, message, Snackbar.LENGTH_LONG).show()
    }

    fun showConfirmationDialog(
        anchor: View,
        title: String,
        message: String,
        positiveActionText: String,
        negativeActionText: String,
        onConfirmed: () -> Unit
    ) {
        AlertDialog.Builder(anchor.context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveActionText) { _, _ -> onConfirmed() }
            .setNegativeButton(negativeActionText, null)
            .show()
    }
}
