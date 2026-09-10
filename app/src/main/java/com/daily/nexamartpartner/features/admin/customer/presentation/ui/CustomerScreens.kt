package com.daily.nexamartpartner.features.admin.customer.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.core.widgets.UiFeedback
import com.daily.nexamartpartner.databinding.FragmentAdminCustomerDetailsBinding
import com.daily.nexamartpartner.databinding.FragmentAdminCustomersBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.admin.customer.domain.model.*
import com.daily.nexamartpartner.features.admin.customer.presentation.state.*
import com.daily.nexamartpartner.features.admin.customer.presentation.viewmodel.*
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import kotlinx.coroutines.launch

class CustomerListScreen : Fragment(R.layout.fragment_admin_customers) {
    private var _binding: FragmentAdminCustomersBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val vm: CustomerListViewModel by viewModels { CustomerListViewModelFactory(requireContext().appContainer.provideGetCustomersUseCase()) }
    private val adapter = CustomerAdapter { findNavController().navigate(R.id.adminCustomerDetailsFragment, Bundle().apply { putString("customerId", it.customerId) }) }
    private val authVm: AuthCoordinatorViewModel by viewModels { AuthCoordinatorViewModelFactory(requireContext().appContainer.restoreSessionUseCase, requireContext().appContainer.logoutUseCase, requireContext().appContainer.authStateStore) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAdminCustomersBinding.bind(view)
        binding.customersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.customersRecyclerView.adapter = adapter
        binding.customersRecyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(r: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && r.layoutManager is LinearLayoutManager) {
                    val m = r.layoutManager as LinearLayoutManager
                    if (m.findLastVisibleItemPosition() >= m.itemCount - 3) vm.next()
                }
            }
        })
        binding.customersSearchInput.addTextChangedListener { vm.search(it?.toString().orEmpty()) }
        binding.customerStatusAllChip.setOnClickListener { vm.setStatus(null) }
        binding.customerStatusActiveChip.setOnClickListener { vm.setStatus(CustomerAccountStatus.ACTIVE) }
        binding.customerStatusInactiveChip.setOnClickListener { vm.setStatus(CustomerAccountStatus.INACTIVE) }
        binding.customerStatusSuspendedChip.setOnClickListener { vm.setStatus(CustomerAccountStatus.SUSPENDED) }
        binding.customersClearButton.setOnClickListener { binding.customersSearchInput.setText(""); vm.clear() }
        binding.customersBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.customersRefreshButton.setOnClickListener { vm.refresh() }
        binding.customersSwipeRefresh.setOnRefreshListener { vm.refresh() }
        binding.customersRetryButton.setOnClickListener { vm.retry() }
        collect()
    }

    private fun collect() {
        viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { vm.uiState.collect(::render) } }
        viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { vm.events.collect { event -> when (event) { CustomerEvent.SessionExpired -> authVm.onSessionExpired(); is CustomerEvent.Message -> UiFeedback.showSnackbar(binding.root, event.text); else -> Unit } } } }
    }

    private fun render(s: CustomerListUiState) {
        binding.customersSwipeRefresh.isRefreshing = s.isRefreshing
        binding.customersLoading.isVisible = s.content is CustomerListContent.Loading
        binding.customersRecyclerView.isVisible = s.content is CustomerListContent.Success
        binding.customersStateSection.isVisible = s.content !is CustomerListContent.Success && !binding.customersLoading.isVisible
        when (val c = s.content) {
            is CustomerListContent.Success -> { adapter.submitList(c.items); binding.customersStateTitle.text = "Customers"; binding.customersStateMessage.text = ""; binding.customersRetryButton.isVisible = false }
            is CustomerListContent.Empty -> { binding.customersStateTitle.text = "No customers"; binding.customersStateMessage.text = c.message; binding.customersRetryButton.isVisible = false }
            is CustomerListContent.Error -> { binding.customersStateTitle.text = "Unable to load customers"; binding.customersStateMessage.text = c.message; binding.customersRetryButton.isVisible = true }
            is CustomerListContent.Unavailable -> { binding.customersStateTitle.text = "Customer API unavailable"; binding.customersStateMessage.text = c.message; binding.customersRetryButton.isVisible = false }
            CustomerListContent.Loading -> Unit
        }
    }
    override fun onDestroyView() { binding.customersRecyclerView.adapter = null; _binding = null; super.onDestroyView() }
}

class CustomerDetailsScreen : Fragment(R.layout.fragment_admin_customer_details) {
    private var _binding: FragmentAdminCustomerDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val id by lazy { requireArguments().getString("customerId").orEmpty() }
    private val vm: CustomerDetailsViewModel by viewModels { CustomerDetailsViewModelFactory(id, requireContext().appContainer.provideGetCustomerDetailsUseCase(), requireContext().appContainer.providePerformCustomerAdminActionUseCase()) }
    private val authVm: AuthCoordinatorViewModel by viewModels { AuthCoordinatorViewModelFactory(requireContext().appContainer.restoreSessionUseCase, requireContext().appContainer.logoutUseCase, requireContext().appContainer.authStateStore) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAdminCustomerDetailsBinding.bind(view)
        binding.customerDetailsBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.customerDetailsRefreshButton.setOnClickListener { vm.refresh() }
        binding.customerDetailsRetryButton.setOnClickListener { vm.retry() }
        collect()
    }

    private fun collect() {
        viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { vm.uiState.collect(::render) } }
        viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) { vm.events.collect { event -> when (event) { CustomerEvent.SessionExpired -> authVm.onSessionExpired(); is CustomerEvent.Message -> UiFeedback.showSnackbar(binding.root, event.text); is CustomerEvent.ActionSucceeded -> { if (event.action == CustomerAdminAction.DEACTIVATE || event.action == CustomerAdminAction.SUSPEND) UiFeedback.showSnackbar(binding.root, "Customer status updated") } } } } }
    }

    private fun render(s: CustomerDetailsUiState) {
        binding.customerDetailsLoading.isVisible = s.content is CustomerDetailsContent.Loading
        binding.customerDetailsContent.isVisible = s.content is CustomerDetailsContent.Success
        binding.customerDetailsStateSection.isVisible = s.content is CustomerDetailsContent.Error || s.content is CustomerDetailsContent.Unavailable
        when (val c = s.content) {
            is CustomerDetailsContent.Success -> {
                val x = c.customer
                binding.customerDetailsName.text = x.name.ifBlank { "Customer" }
                binding.customerDetailsContact.text = listOfNotNull(x.phone, x.email).joinToString("\n").ifBlank { "Contact unavailable" }
                binding.customerDetailsStatus.text = x.accountStatus?.backendValue ?: "Status unavailable"
                binding.customerDetailsMeta.text = buildString {
                    x.registeredAt?.let { append("Registered: ").append(it).append('\n') }
                    x.lastActiveAt?.let { append("Last active: ").append(it).append('\n') }
                    x.orderCount?.let { append("Orders: ").append(it).append('\n') }
                    x.totalSpent?.let { append("Total spent: ").append(ValueFormatter.formatCurrency(BigDecimal.valueOf(it), x.currencyCode)) }
                }.trim()
                binding.customerDetailsAddress.text = x.defaultAddress?.takeIf { it.isNotBlank() } ?: "Address unavailable"
                binding.customerDetailsActions.removeAllViews()
                x.allowedActions.forEach { a ->
                    binding.customerDetailsActions.addView(MaterialButton(requireContext()).apply {
                        text = a.backendValue
                        isEnabled = s.actionInProgress == null
                        setOnClickListener { UiFeedback.showConfirmationDialog(binding.root, a.backendValue, "Confirm ${a.backendValue.lowercase()} for ${x.name.ifBlank { "this customer" }}?", getString(R.string.common_confirm), getString(R.string.common_cancel)) { vm.perform(a) } }
                    })
                }
            }
            is CustomerDetailsContent.Error -> { binding.customerDetailsStateTitle.text = "Unable to load customer"; binding.customerDetailsStateMessage.text = c.message }
            is CustomerDetailsContent.Unavailable -> { binding.customerDetailsStateTitle.text = "Customer API unavailable"; binding.customerDetailsStateMessage.text = c.message }
            CustomerDetailsContent.Loading -> Unit
        }
    }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
