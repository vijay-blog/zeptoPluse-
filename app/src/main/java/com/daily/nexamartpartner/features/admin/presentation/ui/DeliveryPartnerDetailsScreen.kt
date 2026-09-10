package com.daily.nexamartpartner.features.admin.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.core.widgets.UiFeedback
import com.daily.nexamartpartner.databinding.FragmentDeliveryPartnerDetailsBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerDetails
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.PartnerOrderSummary
import com.daily.nexamartpartner.features.admin.presentation.state.DeliveryPartnerDetailsUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerDetailsViewModel
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerDetailsViewModelFactory
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerEvent
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class DeliveryPartnerDetailsScreen : Fragment(R.layout.fragment_delivery_partner_details) {
    private var _binding: FragmentDeliveryPartnerDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val partnerId by lazy { requireArguments().getString("partnerId").orEmpty() }
    private val authViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            requireContext().appContainer.restoreSessionUseCase,
            requireContext().appContainer.logoutUseCase,
            requireContext().appContainer.authStateStore
        )
    }
    private val viewModel: DeliveryPartnerDetailsViewModel by viewModels {
        DeliveryPartnerDetailsViewModelFactory(
            partnerId,
            requireContext().appContainer.provideDeliveryPartnerDetailsUseCase(),
            requireContext().appContainer.provideUpdateDeliveryPartnerUseCase()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeliveryPartnerDetailsBinding.bind(view)
        binding.partnerBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.partnerDetailsRefresh.setOnRefreshListener(viewModel::refresh)
        binding.partnerDetailsRefreshButton.setOnClickListener { viewModel.refresh() }
        binding.partnerDetailsRetry.setOnClickListener { viewModel.retry() }
        collectState()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect(::render) }
                launch {
                    viewModel.events.collect {
                        when (it) {
                            DeliveryPartnerEvent.SessionExpired -> authViewModel.onSessionExpired()
                            is DeliveryPartnerEvent.Message -> UiFeedback.showSnackbar(binding.root, it.text)
                        }
                    }
                }
            }
        }
    }

    private fun render(state: DeliveryPartnerDetailsUiState) {
        binding.partnerDetailsRefresh.isRefreshing = state.isRefreshing
        binding.partnerDetailsLoading.isVisible = state.content is DeliveryPartnerDetailsUiState.Content.Loading
        binding.partnerDetailsContent.isVisible = state.content is DeliveryPartnerDetailsUiState.Content.Success
        binding.partnerDetailsState.isVisible =
            state.content is DeliveryPartnerDetailsUiState.Content.Error ||
            state.content is DeliveryPartnerDetailsUiState.Content.Unavailable
        when (val content = state.content) {
            is DeliveryPartnerDetailsUiState.Content.Success -> renderPartner(content.partner, state.actionInProgress)
            is DeliveryPartnerDetailsUiState.Content.Error -> {
                binding.partnerDetailsStateTitle.text = getString(R.string.delivery_partner_error_title)
                binding.partnerDetailsStateMessage.text = content.message
            }
            is DeliveryPartnerDetailsUiState.Content.Unavailable -> {
                binding.partnerDetailsStateTitle.text = getString(R.string.delivery_partner_unavailable_title)
                binding.partnerDetailsStateMessage.text = content.message
            }
            DeliveryPartnerDetailsUiState.Content.Loading -> Unit
        }
    }

    private fun renderPartner(partner: DeliveryPartnerDetails, processing: PartnerAdminAction?) {
        binding.partnerAvatarText.text = initials(partner.name)
        binding.partnerNameText.text = partner.name
        binding.partnerProfileText.text = getString(
            R.string.delivery_partner_profile_template,
            partner.phone ?: unavailable(),
            partner.email ?: unavailable(),
            partner.registeredAt ?: unavailable(),
            partner.lastActiveAt ?: unavailable()
        )
        binding.partnerAccountText.text = getString(
            R.string.delivery_partner_account_template,
            partner.accountStatus.backendValue,
            partner.verificationStatus.backendValue
        )
        binding.partnerAvailabilityText.text = getString(
            R.string.delivery_partner_availability_template,
            partner.availability.backendValue,
            partner.workState.backendValue
        )
        binding.partnerAssignableText.text = when (partner.isAssignable) {
            true -> getString(R.string.delivery_partner_assignable_yes)
            false -> getString(R.string.delivery_partner_assignable_no)
            null -> getString(R.string.delivery_partner_assignable_unknown)
        }
        binding.partnerVehicleText.text = getString(
            R.string.delivery_partner_vehicle_template,
            partner.vehicleType ?: unavailable(),
            partner.vehicleNumber ?: unavailable(),
            partner.licenseReference ?: unavailable()
        )
        val stats = partner.statistics
        binding.partnerStatisticsText.text = if (stats == null) {
            getString(R.string.delivery_partner_statistics_unavailable)
        } else getString(
            R.string.delivery_partner_statistics_template,
            format(stats.totalDeliveries),
            format(stats.completedDeliveries),
            format(stats.cancelledDeliveries),
            format(stats.activeDeliveries)
        )
        renderOrders(binding.currentOrdersContainer, partner.currentOrders, clickable = true)
        renderOrders(binding.historyContainer, partner.recentHistory, clickable = false)
        renderActions(partner, processing)
    }

    private fun renderOrders(
        container: android.widget.LinearLayout,
        orders: List<PartnerOrderSummary>,
        clickable: Boolean
    ) {
        container.removeAllViews()
        if (orders.isEmpty()) {
            container.addView(TextView(requireContext()).apply {
                text = getString(R.string.delivery_partner_no_orders)
            })
            return
        }
        orders.forEach { order ->
            container.addView(MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = getString(
                    R.string.delivery_partner_order_template,
                    order.orderId,
                    order.status.backendValue,
                    order.timestamp ?: unavailable()
                )
                isAllCaps = false
                isEnabled = clickable
                if (clickable) setOnClickListener {
                    val args = Bundle().apply { putString("orderId", order.orderId) }
                    findNavController().navigate(R.id.adminOrderDetailsFragment, args)
                }
            })
        }
    }

    private fun renderActions(partner: DeliveryPartnerDetails, processing: PartnerAdminAction?) {
        binding.partnerActionsContainer.removeAllViews()
        if (partner.allowedActions.isEmpty()) {
            binding.partnerActionsContainer.addView(TextView(requireContext()).apply {
                text = getString(R.string.delivery_partner_no_actions)
            })
            return
        }
        partner.allowedActions.forEach { action ->
            binding.partnerActionsContainer.addView(MaterialButton(requireContext()).apply {
                text = action.backendValue
                isEnabled = processing == null
                setOnClickListener { confirmAction(partner, action) }
            })
        }
    }

    private fun confirmAction(partner: DeliveryPartnerDetails, action: PartnerAdminAction) {
        UiFeedback.showConfirmationDialog(
            anchor = binding.root,
            title = action.backendValue,
            message = getString(
                R.string.delivery_partner_action_confirmation,
                partner.accountStatus.backendValue,
                action.backendValue
            ),
            positiveActionText = getString(R.string.common_confirm),
            negativeActionText = getString(R.string.common_cancel),
            onConfirmed = { viewModel.performAction(action) }
        )
    }

    private fun initials(name: String): String =
        name.trim().split(Regex("\\s+")).filter(String::isNotBlank).take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifBlank { "DP" }

    private fun unavailable() = getString(R.string.delivery_partner_value_unavailable)
    private fun format(value: Long?) = value?.let(ValueFormatter::formatCount) ?: unavailable()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
