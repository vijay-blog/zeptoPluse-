package com.daily.nexamartpartner.features.delivery.presentation.ui

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
import com.daily.nexamartpartner.databinding.FragmentDeliveryAssignedOrdersBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.*
import com.daily.nexamartpartner.features.delivery.presentation.state.DeliveryOrdersUiState
import com.daily.nexamartpartner.features.delivery.presentation.viewmodel.*
import com.daily.nexamartpartner.routing.ProtectedNavigator
import kotlinx.coroutines.launch

class DeliveryAssignedOrdersScreen:Fragment(R.layout.fragment_delivery_assigned_orders){
 private var _binding:FragmentDeliveryAssignedOrdersBinding?=null;private val b get()=requireNotNull(_binding)
 private val vm:DeliveryOrdersViewModel by viewModels{DeliveryOrdersViewModelFactory(requireContext().appContainer.provideGetAssignedDeliveryOrdersUseCase())}
 private val auth:AuthCoordinatorViewModel by activityViewModels{AuthCoordinatorViewModelFactory(requireContext().appContainer.restoreSessionUseCase,requireContext().appContainer.logoutUseCase,requireContext().appContainer.authStateStore)}
 private val adapter=DeliveryOrderAdapter{findNavController().navigate(R.id.deliveryOrderDetailsFragment,bundleOf("orderId" to it.orderId))}
 private val watcher=object:TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){};override fun afterTextChanged(e:Editable?){vm.search(e?.toString().orEmpty())}}
 override fun onViewCreated(v:View,s:Bundle?){super.onViewCreated(v,s);_binding=FragmentDeliveryAssignedOrdersBinding.bind(v);b.recyclerView.layoutManager=LinearLayoutManager(requireContext());b.recyclerView.adapter=adapter;b.searchEditText.addTextChangedListener(watcher);b.swipeRefresh.setOnRefreshListener{vm.refresh()};b.retryButton.setOnClickListener{vm.retry()};b.recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){override fun onScrolled(r:RecyclerView,dx:Int,dy:Int){if(dy>0){val lm=r.layoutManager as LinearLayoutManager;if(lm.findLastVisibleItemPosition()>=lm.itemCount-4)vm.nextPage()}}});collect()}
 private fun collect(){viewLifecycleOwner.lifecycleScope.launch{viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){launch{vm.state.collect{render(it)}};launch{vm.events.collect{if(it is DeliveryOrdersViewModel.Event.SessionExpired)auth.onSessionExpired()}}}}}
 private fun render(s:DeliveryOrdersUiState){b.swipeRefresh.isRefreshing=s.isRefreshing;b.paginationProgress.isVisible=s.isLoadingMore;when(val c=s.content){DeliveryOrdersUiState.Content.Loading->{b.loading.isVisible=true;b.recyclerView.isVisible=false;b.stateCard.isVisible=false};is DeliveryOrdersUiState.Content.Success->{b.loading.isVisible=false;b.recyclerView.isVisible=true;b.stateCard.isVisible=false;adapter.submitList(c.orders)};is DeliveryOrdersUiState.Content.Empty->state(c.title,c.message);is DeliveryOrdersUiState.Content.Error->state(c.title,c.message);is DeliveryOrdersUiState.Content.Unavailable->state(c.title,c.message)}}
 private fun state(t:String,m:String){b.loading.isVisible=false;b.recyclerView.isVisible=false;b.stateCard.isVisible=true;b.stateTitle.text=t;b.stateMessage.text=m}
 override fun onDestroyView(){b.searchEditText.removeTextChangedListener(watcher);b.recyclerView.adapter=null;_binding=null;super.onDestroyView()}
}
