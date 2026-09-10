package com.daily.nexamartpartner.features.delivery.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentDeliveryDashboardPlaceholderBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.daily.nexamartpartner.routing.ProtectedNavigator

class DeliveryDashboardPlaceholderFragment :
    Fragment(R.layout.fragment_delivery_dashboard_placeholder) {
    private var _binding: FragmentDeliveryDashboardPlaceholderBinding? = null
    private val binding: FragmentDeliveryDashboardPlaceholderBinding
        get() = requireNotNull(_binding)

    private val authCoordinatorViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            restoreSessionUseCase = requireContext().appContainer.restoreSessionUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase,
            authStateStore = requireContext().appContainer.authStateStore
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeliveryDashboardPlaceholderBinding.bind(view)
        val navigator = ProtectedNavigator(
            navController = findNavController(),
            authStateStore = requireContext().appContainer.authStateStore
        )
        binding.assignedOrdersButton.setOnClickListener { navigator.navigate(R.id.deliveryAssignedOrdersFragment) }
        binding.orderDetailsButton.setOnClickListener { navigator.navigate(R.id.deliveryAssignedOrdersFragment) }
        binding.pickupButton.setOnClickListener { navigator.navigate(R.id.deliveryPickupPlaceholderFragment) }
        binding.deliveryButton.setOnClickListener { navigator.navigate(R.id.deliveryRunPlaceholderFragment) }
        binding.historyButton.setOnClickListener { navigator.navigate(R.id.deliveryHistoryFragment) }
        binding.earningsButton.setOnClickListener { navigator.navigate(R.id.deliveryEarningsPlaceholderFragment) }
        binding.profileButton.setOnClickListener { navigator.navigate(R.id.deliveryProfilePlaceholderFragment) }
        binding.availabilityButton.setOnClickListener { navigator.navigate(R.id.deliveryAvailabilityPlaceholderFragment) }
        binding.notificationsButton.setOnClickListener { navigator.navigate(R.id.deliveryNotificationsPlaceholderFragment) }
        binding.logoutButton.setOnClickListener {
            authCoordinatorViewModel.logout()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
