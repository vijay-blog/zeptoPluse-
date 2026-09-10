package com.daily.nexamartpartner.features.delivery.presentation.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.*
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentDeliveryHistoryBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.*
import com.daily.nexamartpartner.features.delivery.presentation.state.DeliveryHistoryUiState
import com.daily.nexamartpartner.features.delivery.presentation.viewmodel.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DeliveryHistoryScreen:Fragment(R.layout.fragment_delivery_history){
 private var _binding:FragmentDeliveryHistoryBinding?=null; private val b get()=requireNotNull(_binding)
 private val vm:DeliveryHistoryViewModel by viewModels{DeliveryHistoryViewModelFactory(requireContext().appContainer.provideGetDeliveryHistoryUseCase())}
 private val auth:AuthCoordinatorViewModel by activityViewModels{AuthCoordinatorViewModelFactory(requireContext().appContainer.restoreSessionUseCase,requireContext().appContainer.logoutUseCase,requireContext().appContainer.authStateStore)}
 private val adapter=DeliveryOrderAdapter{findNavController().navigate(R.id.deliveryOrderDetailsFragment,bundleOf("orderId" to it.orderId))}
 private val watcher=object:TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){};override fun afterTextChanged(e:Editable?){vm.search(e?.toString().orEmpty())}}
 private val fmt=SimpleDateFormat("yyyy-MM-dd",Locale.US)
 override fun onViewCreated(v:View,s:Bundle?){super.onViewCreated(v,s);_binding=FragmentDeliveryHistoryBinding.bind(v);b.recyclerView.layoutManager=LinearLayoutManager(requireContext());b.recyclerView.adapter=adapter;b.searchEditText.addTextChangedListener(watcher);b.swipeRefresh.setOnRefreshListener{vm.refresh()};b.retryButton.setOnClickListener{vm.retry()};b.statusAllButton.setOnClickListener{vm.setStatus(null)};b.statusDeliveredButton.setOnClickListener{vm.setStatus("DELIVERED")};b.statusCancelledButton.setOnClickListener{vm.setStatus("CANCELLED")};b.fromDateButton.setOnClickListener{pick(true)};b.toDateButton.setOnClickListener{pick(false)};b.clearDatesButton.setOnClickListener{vm.setDateRange(null,null);b.fromDateButton.text="From date";b.toDateButton.text="To date"};b.recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){override fun onScrolled(r:RecyclerView,dx:Int,dy:Int){if(dy>0){val lm=r.layoutManager as LinearLayoutManager;if(lm.findLastVisibleItemPosition()>=lm.itemCount-4)vm.nextPage()}}});collect()}
 private fun pick(from:Boolean){val now=Calendar.getInstance();DatePickerDialog(requireContext(),{_,y,m,d->val c=Calendar.getInstance().apply{set(y,m,d)};val value=fmt.format(c.time);val f=if(from)value else vm.state.value.filters.fromDate;val t=if(from)vm.state.value.filters.toDate else value;if(f!=null&&t!=null&&f>t)return@DatePickerDialog;vm.setDateRange(f,t);if(from)b.fromDateButton.text=value else b.toDateButton.text=value},now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH)).show()}
 private fun collect(){viewLifecycleOwner.lifecycleScope.launch{viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){launch{vm.state.collect{render(it)}};launch{vm.events.collect{if(it is DeliveryHistoryViewModel.Event.SessionExpired)auth.onSessionExpired()}}}}}
 private fun render(s:DeliveryHistoryUiState){b.swipeRefresh.isRefreshing=s.isRefreshing;b.paginationProgress.isVisible=s.isLoadingMore;when(val c=s.content){DeliveryHistoryUiState.Content.Loading->{b.loading.isVisible=true;b.recyclerView.isVisible=false;b.stateCard.isVisible=false};is DeliveryHistoryUiState.Content.Success->{b.loading.isVisible=false;b.recyclerView.isVisible=true;b.stateCard.isVisible=false;adapter.submitList(c.orders)};is DeliveryHistoryUiState.Content.Empty->state(c.title,c.message);is DeliveryHistoryUiState.Content.Error->state(c.title,c.message);is DeliveryHistoryUiState.Content.Unavailable->state(c.title,c.message)}}
 private fun state(t:String,m:String){b.loading.isVisible=false;b.recyclerView.isVisible=false;b.stateCard.isVisible=true;b.stateTitle.text=t;b.stateMessage.text=m}
 override fun onDestroyView(){b.searchEditText.removeTextChangedListener(watcher);b.recyclerView.adapter=null;_binding=null;super.onDestroyView()}
}
