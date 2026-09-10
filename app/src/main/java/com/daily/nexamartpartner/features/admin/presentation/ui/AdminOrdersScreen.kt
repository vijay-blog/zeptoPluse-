package com.daily.nexamartpartner.features.admin.presentation.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import com.daily.nexamartpartner.databinding.FragmentAdminOrdersBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.PaymentStatus
import com.daily.nexamartpartner.features.admin.presentation.state.AdminOrdersUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.AdminOrdersViewModel
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.AdminOrdersViewModelFactory
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import kotlinx.coroutines.launch

class AdminOrdersScreen : Fragment(R.layout.fragment_admin_orders) {
    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding: FragmentAdminOrdersBinding
        get() = requireNotNull(_binding)

    private val authCoordinatorViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            restoreSessionUseCase = requireContext().appContainer.restoreSessionUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase,
            authStateStore = requireContext().appContainer.authStateStore
        )
    }

    private val viewModel: AdminOrdersViewModel by viewModels {
        AdminOrdersViewModelFactory(requireContext().appContainer.provideAdminOrdersUseCase())
    }

    private val ordersAdapter = AdminOrdersAdapter { order ->
        findNavController().navigate(
            R.id.adminOrderDetailsFragment,
            bundleOf("orderId" to order.orderId)
        )
    }

    private val searchWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            viewModel.onSearchQueryChanged(s?.toString().orEmpty())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminOrdersBinding.bind(view)
        setupRecyclerView()
        setupActions()
        collectUi()
        collectEvents()
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.ordersRecyclerView.layoutManager = manager
        binding.ordersRecyclerView.adapter = ordersAdapter
        binding.ordersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val total = manager.itemCount
                val lastVisible = manager.findLastVisibleItemPosition()
                if (lastVisible >= total - 4) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setupActions() {
        binding.searchInputEditText.addTextChangedListener(searchWatcher)
        binding.ordersSwipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.ordersRefreshButton.setOnClickListener { viewModel.refresh() }
        binding.ordersRetryButton.setOnClickListener { viewModel.retry() }
        binding.clearFiltersButton.setOnClickListener { viewModel.clearFilters() }
        binding.filterButton.setOnClickListener { showFilterDialog() }

        binding.chipAll.setOnClickListener { viewModel.onStatusFilterSelected(null) }
        binding.chipPending.setOnClickListener { viewModel.onStatusFilterSelected(OrderStatus.PENDING) }
        binding.chipPreparing.setOnClickListener { viewModel.onStatusFilterSelected(OrderStatus.PREPARING) }
        binding.chipReady.setOnClickListener { viewModel.onStatusFilterSelected(OrderStatus.READY) }
    }

    private fun showFilterDialog() {
        val sortOptions = AdminOrderSort.entries.map { it.name }.toTypedArray()
        val paymentOptions = arrayOf("All Payments", "PENDING", "PAID", "FAILED", "REFUNDED")
        var selectedSort = viewModel.uiState.value.selectedSort
        var selectedPayment = viewModel.uiState.value.filters.paymentStatus

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.admin_orders_filter))
            .setSingleChoiceItems(
                sortOptions,
                sortOptions.indexOf(selectedSort.name)
            ) { _, which ->
                selectedSort = AdminOrderSort.valueOf(sortOptions[which])
            }
            .setNeutralButton(getString(R.string.admin_orders_payment_filter)) { _, _ ->
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.admin_orders_payment_filter))
                    .setSingleChoiceItems(
                        paymentOptions,
                        paymentOptions.indexOf(selectedPayment?.backendValue ?: "All Payments")
                    ) { dialog, which ->
                        selectedPayment = when (paymentOptions[which]) {
                            "PENDING" -> PaymentStatus.PENDING
                            "PAID" -> PaymentStatus.PAID
                            "FAILED" -> PaymentStatus.FAILED
                            "REFUNDED" -> PaymentStatus.REFUNDED
                            else -> null
                        }
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.common_confirm), null)
                    .show()
            }
            .setNegativeButton(getString(R.string.common_cancel), null)
            .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
                viewModel.onSortSelected(selectedSort)
                viewModel.onPaymentFilterSelected(selectedPayment)
            }
            .show()
    }

    private fun collectUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AdminOrdersViewModel.Event.SessionExpired -> authCoordinatorViewModel.onSessionExpired()
                        is AdminOrdersViewModel.Event.ShowMessage -> Unit
                    }
                }
            }
        }
    }

    private fun render(state: AdminOrdersUiState) {
        binding.ordersSwipeRefresh.isRefreshing = state.isRefreshing
        binding.paginationProgress.isVisible = state.isLoadingMore
        when (val content = state.content) {
            is AdminOrdersUiState.ContentState.Loading -> {
                binding.ordersLoadingSection.isVisible = true
                binding.ordersRecyclerView.isVisible = false
                binding.ordersStateSection.isVisible = false
            }

            is AdminOrdersUiState.ContentState.Success -> {
                binding.ordersLoadingSection.isVisible = false
                binding.ordersRecyclerView.isVisible = true
                binding.ordersStateSection.isVisible = false
                ordersAdapter.submitList(content.orders)
            }

            is AdminOrdersUiState.ContentState.Empty -> {
                binding.ordersLoadingSection.isVisible = false
                binding.ordersRecyclerView.isVisible = false
                binding.ordersStateSection.isVisible = true
                binding.ordersStateTitleText.text = content.title
                binding.ordersStateDescriptionText.text = content.message
                binding.clearFiltersButton.isVisible = content.showClearFilters
            }

            is AdminOrdersUiState.ContentState.Error -> {
                binding.ordersLoadingSection.isVisible = false
                binding.ordersRecyclerView.isVisible = false
                binding.ordersStateSection.isVisible = true
                binding.ordersStateTitleText.text = content.title
                binding.ordersStateDescriptionText.text = content.message
                binding.clearFiltersButton.isVisible = false
            }

            is AdminOrdersUiState.ContentState.Unavailable -> {
                binding.ordersLoadingSection.isVisible = false
                binding.ordersRecyclerView.isVisible = false
                binding.ordersStateSection.isVisible = true
                binding.ordersStateTitleText.text = content.title
                binding.ordersStateDescriptionText.text = content.message
                binding.clearFiltersButton.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        binding.searchInputEditText.removeTextChangedListener(searchWatcher)
        binding.ordersRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
