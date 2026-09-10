package com.daily.nexamartpartner.features.delivery.notifications.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentDeliveryNotificationsBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotification
import com.daily.nexamartpartner.features.delivery.notifications.presentation.state.*
import com.daily.nexamartpartner.features.delivery.notifications.presentation.viewmodel.*
import kotlinx.coroutines.launch

class DeliveryNotificationsScreen : Fragment(R.layout.fragment_delivery_notifications) {
    private var _binding: FragmentDeliveryNotificationsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: DeliveryNotificationsViewModel by viewModels {
        DeliveryNotificationsViewModelFactory(
            requireContext().appContainer.provideGetDeliveryNotificationsUseCase(),
            requireContext().appContainer.provideMarkDeliveryNotificationReadUseCase(),
            requireContext().appContainer.provideMarkAllDeliveryNotificationsReadUseCase()
        )
    }
    private val authViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(requireContext().appContainer.restoreSessionUseCase, requireContext().appContainer.logoutUseCase, requireContext().appContainer.authStateStore)
    }
    private val adapter = DeliveryNotificationAdapter { notification ->
        viewModel.markRead(notification.id)
        notification.orderId?.takeIf { it.isNotBlank() }?.let {
            findNavController().navigate(R.id.deliveryOrderDetailsFragment, bundleOf("orderId" to it))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeliveryNotificationsBinding.bind(view)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.retryButton.setOnClickListener { viewModel.retry() }
        binding.markAllReadButton.setOnClickListener { viewModel.markAllRead() }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val lm = rv.layoutManager as LinearLayoutManager
                    if (lm.findLastVisibleItemPosition() >= lm.itemCount - 4) viewModel.nextPage()
                }
            }
        })
        collect()
    }

    private fun collect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect(::render) }
                launch { viewModel.events.collect { if (it is DeliveryNotificationsViewModel.Event.SessionExpired) authViewModel.onSessionExpired() } }
            }
        }
    }

    private fun render(state: DeliveryNotificationsUiState) {
        binding.swipeRefresh.isRefreshing = state.isRefreshing
        binding.paginationProgress.isVisible = state.isLoadingMore
        binding.unreadCountText.text = getString(R.string.delivery_notifications_unread_count, state.unreadCount)
        binding.markAllReadButton.isVisible = state.unreadCount > 0
        when (val content = state.content) {
            NotificationContent.Loading -> { binding.loading.isVisible = true; binding.recyclerView.isVisible = false; binding.stateCard.isVisible = false }
            is NotificationContent.Success -> { binding.loading.isVisible = false; binding.recyclerView.isVisible = true; binding.stateCard.isVisible = false; adapter.submitList(content.items) }
            is NotificationContent.Empty -> showState(content.title, content.message, false)
            is NotificationContent.Error -> showState(content.title, content.message, true)
            is NotificationContent.Unavailable -> showState(content.title, content.message, false)
        }
    }

    private fun showState(title: String, message: String, retry: Boolean) {
        binding.loading.isVisible = false; binding.recyclerView.isVisible = false; binding.stateCard.isVisible = true
        binding.stateTitle.text = title; binding.stateMessage.text = message; binding.retryButton.isVisible = retry
    }

    override fun onDestroyView() { binding.recyclerView.adapter = null; _binding = null; super.onDestroyView() }
}
