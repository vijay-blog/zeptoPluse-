package com.daily.nexamartpartner.features.delivery.earnings.presentation.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.databinding.FragmentDeliveryEarningsBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsSummary
import com.daily.nexamartpartner.features.delivery.earnings.presentation.state.DeliveryEarningsUiState
import com.daily.nexamartpartner.features.delivery.earnings.presentation.viewmodel.DeliveryEarningsViewModel
import com.daily.nexamartpartner.features.delivery.earnings.presentation.viewmodel.DeliveryEarningsViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DeliveryEarningsScreen : Fragment(R.layout.fragment_delivery_earnings) {
    private var _binding: FragmentDeliveryEarningsBinding? = null
    private val b get() = requireNotNull(_binding)
    private val vm: DeliveryEarningsViewModel by viewModels {
        DeliveryEarningsViewModelFactory(requireContext().appContainer.provideGetDeliveryEarningsSummaryUseCase(), requireContext().appContainer.provideGetDeliveryEarningsHistoryUseCase())
    }
    private val auth: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(requireContext().appContainer.restoreSessionUseCase, requireContext().appContainer.logoutUseCase, requireContext().appContainer.authStateStore)
    }
    private val adapter = DeliveryEarningAdapter()
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeliveryEarningsBinding.bind(view)
        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter
        b.swipeRefresh.setOnRefreshListener { vm.refresh() }
        b.fromDateButton.setOnClickListener { pickDate(true) }
        b.toDateButton.setOnClickListener { pickDate(false) }
        b.clearDatesButton.setOnClickListener { vm.setDateRange(null, null); b.fromDateButton.text = "From date"; b.toDateButton.text = "To date" }
        b.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val lm = rv.layoutManager as LinearLayoutManager
                    if (lm.findLastVisibleItemPosition() >= lm.itemCount - 4) vm.nextPage()
                }
            }
        })
        collect()
    }

    private fun pickDate(from: Boolean) {
        val now = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            val c = Calendar.getInstance().apply { set(y, m, d) }
            val value = fmt.format(c.time)
            val f = if (from) value else vm.state.value.filters.fromDate
            val t = if (from) vm.state.value.filters.toDate else value
            if (f != null && t != null && f > t) return@DatePickerDialog
            vm.setDateRange(f, t)
            if (from) b.fromDateButton.text = value else b.toDateButton.text = value
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun collect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { vm.state.collect { render(it) } }
                launch { vm.events.collect { if (it is DeliveryEarningsViewModel.Event.SessionExpired) auth.onSessionExpired() } }
            }
        }
    }

    private fun render(s: DeliveryEarningsUiState) {
        b.swipeRefresh.isRefreshing = s.isRefreshing
        b.loading.isVisible = s.isLoading && s.summary is DeliveryEarningsUiState.SummaryState.Loading
        b.paginationProgress.isVisible = s.isLoadingMore
        val f = s.filters
        b.filterSummary.text = when { f.fromDate != null && f.toDate != null -> "Period: ${f.fromDate} to ${f.toDate}"; f.fromDate != null -> "From: ${f.fromDate}"; f.toDate != null -> "Through: ${f.toDate}"; else -> "All time" }
        when (val summary = s.summary) {
            is DeliveryEarningsUiState.SummaryState.Success -> renderSummary(summary.value)
            is DeliveryEarningsUiState.SummaryState.Loading -> {}
            is DeliveryEarningsUiState.SummaryState.Empty -> b.stateText.text = summary.message
            is DeliveryEarningsUiState.SummaryState.Error -> b.stateText.text = "${summary.title}\n${summary.message}"
            is DeliveryEarningsUiState.SummaryState.Unavailable -> b.stateText.text = "${summary.title}\n${summary.message}"
        }
        when (val history = s.history) {
            is DeliveryEarningsUiState.HistoryState.Success -> { adapter.submitList(history.entries); b.recyclerView.isVisible = true; if (history.entries.isNotEmpty()) b.stateText.text = "" }
            is DeliveryEarningsUiState.HistoryState.Loading -> if (!s.isLoadingMore) b.recyclerView.isVisible = false
            is DeliveryEarningsUiState.HistoryState.Empty -> { adapter.submitList(emptyList()); b.recyclerView.isVisible = false; b.stateText.text = "${history.title}\n${history.message}" }
            is DeliveryEarningsUiState.HistoryState.Error -> { b.recyclerView.isVisible = false; b.stateText.text = "${history.title}\n${history.message}" }
            is DeliveryEarningsUiState.HistoryState.Unavailable -> { b.recyclerView.isVisible = false; b.stateText.text = "${history.title}\n${history.message}" }
        }
    }

    private fun renderSummary(s: DeliveryEarningsSummary) {
        val c = s.currencyCode
        fun money(v: java.math.BigDecimal?) = v?.let { ValueFormatter.formatCurrency(it, c) } ?: "Unavailable"
        b.todayValue.text = "Today\n${money(s.today)}"
        b.weekValue.text = "This week\n${money(s.thisWeek)}"
        b.monthValue.text = "This month\n${money(s.thisMonth)}"
        b.completedValue.text = "Completed\n${s.completedDeliveries?.toString() ?: "Unavailable"}"
        b.pendingValue.text = "Pending payout: ${money(s.pendingPayout)}"
        b.totalValue.text = "Total earned: ${money(s.totalEarned)}"
    }

    override fun onDestroyView() { b.recyclerView.adapter = null; _binding = null; super.onDestroyView() }
}
