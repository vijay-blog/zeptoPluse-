package com.daily.nexamartpartner.features.admin.presentation.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import com.daily.nexamartpartner.databinding.FragmentDeliveryPartnerListBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerFilters
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAccountStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAvailability
import com.daily.nexamartpartner.features.admin.domain.model.PartnerVerificationStatus
import com.daily.nexamartpartner.features.admin.presentation.state.DeliveryPartnerListUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerEvent
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerListViewModel
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerListViewModelFactory
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import kotlinx.coroutines.launch

class DeliveryPartnerListScreen : Fragment(R.layout.fragment_delivery_partner_list) {
    private var _binding: FragmentDeliveryPartnerListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val authViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            requireContext().appContainer.restoreSessionUseCase,
            requireContext().appContainer.logoutUseCase,
            requireContext().appContainer.authStateStore
        )
    }
    private val viewModel: DeliveryPartnerListViewModel by viewModels {
        DeliveryPartnerListViewModelFactory(requireContext().appContainer.provideDeliveryPartnersUseCase())
    }
    private val adapter = DeliveryPartnerAdapter {
        val args = Bundle().apply { putString("partnerId", it.partnerId) }
        findNavController().navigate(R.id.adminDeliveryPartnerDetailsFragment, args)
    }
    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) = viewModel.search(s?.toString().orEmpty())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeliveryPartnerListBinding.bind(view)
        val manager = LinearLayoutManager(requireContext())
        binding.partnersRecyclerView.layoutManager = manager
        binding.partnersRecyclerView.adapter = adapter
        binding.partnersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && manager.findLastVisibleItemPosition() >= manager.itemCount - 4) {
                    viewModel.loadNext()
                }
            }
        })
        binding.partnerSearchInput.addTextChangedListener(watcher)
        binding.partnerSwipeRefresh.setOnRefreshListener(viewModel::refresh)
        binding.refreshPartnersButton.setOnClickListener { viewModel.refresh() }
        binding.partnersRetryButton.setOnClickListener { viewModel.retry() }
        binding.partnerClearFiltersButton.setOnClickListener { viewModel.clearFilters() }
        binding.partnerFilterButton.setOnClickListener { showFilters() }
        binding.partnerAllChip.setOnClickListener { viewModel.applyFilters(DeliveryPartnerFilters()) }
        binding.partnerActiveChip.setOnClickListener {
            viewModel.applyFilters(DeliveryPartnerFilters(accountStatus = PartnerAccountStatus.ACTIVE))
        }
        binding.partnerPendingChip.setOnClickListener {
            viewModel.applyFilters(DeliveryPartnerFilters(verificationStatus = PartnerVerificationStatus.PENDING))
        }
        binding.partnerOfflineChip.setOnClickListener {
            viewModel.applyFilters(DeliveryPartnerFilters(availability = PartnerAvailability.OFFLINE))
        }
        collectState()
    }

    private fun showFilters() {
        val options = arrayOf("All", "Active", "Inactive", "Suspended", "Verified", "Pending verification", "Online", "Offline")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.admin_orders_filter)
            .setItems(options) { _, index ->
                val filter = when (index) {
                    1 -> DeliveryPartnerFilters(accountStatus = PartnerAccountStatus.ACTIVE)
                    2 -> DeliveryPartnerFilters(accountStatus = PartnerAccountStatus.INACTIVE)
                    3 -> DeliveryPartnerFilters(accountStatus = PartnerAccountStatus.SUSPENDED)
                    4 -> DeliveryPartnerFilters(verificationStatus = PartnerVerificationStatus.VERIFIED)
                    5 -> DeliveryPartnerFilters(verificationStatus = PartnerVerificationStatus.PENDING)
                    6 -> DeliveryPartnerFilters(availability = PartnerAvailability.ONLINE)
                    7 -> DeliveryPartnerFilters(availability = PartnerAvailability.OFFLINE)
                    else -> DeliveryPartnerFilters()
                }
                viewModel.applyFilters(filter)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect(::render) }
                launch {
                    viewModel.events.collect {
                        if (it is DeliveryPartnerEvent.SessionExpired) authViewModel.onSessionExpired()
                    }
                }
            }
        }
    }

    private fun render(state: DeliveryPartnerListUiState) {
        binding.partnerSwipeRefresh.isRefreshing = state.isRefreshing
        binding.partnersPagination.isVisible = state.isLoadingMore
        binding.partnersLoading.isVisible = state.content is DeliveryPartnerListUiState.Content.Loading
        binding.partnersRecyclerView.isVisible = state.content is DeliveryPartnerListUiState.Content.Success
        binding.partnersStateSection.isVisible =
            state.content is DeliveryPartnerListUiState.Content.Empty ||
            state.content is DeliveryPartnerListUiState.Content.Error ||
            state.content is DeliveryPartnerListUiState.Content.Unavailable
        when (val content = state.content) {
            is DeliveryPartnerListUiState.Content.Success -> adapter.submitList(content.partners)
            is DeliveryPartnerListUiState.Content.Empty -> {
                binding.partnersStateTitle.text = getString(R.string.delivery_partner_empty_title)
                binding.partnersStateMessage.text = content.message
                binding.partnerClearFiltersButton.isVisible = content.showClearFilters
            }
            is DeliveryPartnerListUiState.Content.Error -> {
                binding.partnersStateTitle.text = getString(R.string.delivery_partner_error_title)
                binding.partnersStateMessage.text = content.message
            }
            is DeliveryPartnerListUiState.Content.Unavailable -> {
                binding.partnersStateTitle.text = getString(R.string.delivery_partner_unavailable_title)
                binding.partnersStateMessage.text = content.message
            }
            DeliveryPartnerListUiState.Content.Loading -> Unit
        }
    }

    override fun onDestroyView() {
        binding.partnerSearchInput.removeTextChangedListener(watcher)
        binding.partnersRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
