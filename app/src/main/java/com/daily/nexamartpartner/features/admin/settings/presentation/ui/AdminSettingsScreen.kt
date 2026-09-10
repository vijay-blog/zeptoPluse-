package com.daily.nexamartpartner.features.admin.settings.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.daily.nexamartpartner.BuildConfig
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.config.AppConfig
import com.daily.nexamartpartner.databinding.FragmentAdminSettingsBinding

class AdminSettingsScreen : Fragment(R.layout.fragment_admin_settings) {
    private var _binding: FragmentAdminSettingsBinding? = null
    private val binding: FragmentAdminSettingsBinding
        get() = requireNotNull(_binding)

    private val preferences by lazy {
        requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminSettingsBinding.bind(view)

        setupHeader()
        renderAppConfiguration()
        setupPreferences()
        setupBackendConfiguration()
    }

    private fun setupHeader() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun renderAppConfiguration() {
        binding.environmentValueText.text = AppConfig.environment.name.lowercase().replaceFirstChar { it.uppercase() }
        binding.baseUrlValueText.text = AppConfig.baseUrl
        binding.appVersionValueText.text = getString(
            R.string.admin_settings_version_template,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        binding.networkLoggingValueText.text = if (AppConfig.enableNetworkLogging) {
            getString(R.string.admin_settings_enabled)
        } else {
            getString(R.string.admin_settings_disabled)
        }
    }

    private fun setupPreferences() {
        binding.notificationsSwitch.isChecked = preferences.getBoolean(KEY_NOTIFICATIONS, true)
        binding.autoRefreshSwitch.isChecked = preferences.getBoolean(KEY_AUTO_REFRESH, true)
        binding.confirmActionsSwitch.isChecked = preferences.getBoolean(KEY_CONFIRM_ACTIONS, true)

        binding.notificationsSwitch.setOnCheckedChangeListener { _, enabled ->
            preferences.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        }
        binding.autoRefreshSwitch.setOnCheckedChangeListener { _, enabled ->
            preferences.edit().putBoolean(KEY_AUTO_REFRESH, enabled).apply()
        }
        binding.confirmActionsSwitch.setOnCheckedChangeListener { _, enabled ->
            preferences.edit().putBoolean(KEY_CONFIRM_ACTIONS, enabled).apply()
        }
    }

    private fun setupBackendConfiguration() {
        binding.backendSettingsStateText.text = getString(R.string.admin_settings_backend_pending)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val PREFERENCES_NAME = "admin_settings"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_AUTO_REFRESH = "auto_refresh_enabled"
        private const val KEY_CONFIRM_ACTIONS = "confirm_actions_enabled"
    }
}
