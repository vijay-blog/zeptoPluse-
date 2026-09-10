package com.daily.nexamartpartner.core.widgets

import com.google.android.material.button.MaterialButton

object LoadingButtonController {
    fun setLoading(button: MaterialButton, loading: Boolean, idleText: String) {
        button.isEnabled = !loading
        button.text = if (loading) "Please wait..." else idleText
    }
}
