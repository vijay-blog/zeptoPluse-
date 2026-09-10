package com.daily.nexamartpartner.features.admin.customer.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.databinding.ItemAdminCustomerBinding
import com.daily.nexamartpartner.features.admin.customer.domain.model.Customer

class CustomerAdapter(private val onClick: (Customer) -> Unit) : ListAdapter<Customer, CustomerAdapter.VH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemAdminCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick)
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
    class VH(private val b: ItemAdminCustomerBinding, private val onClick: (Customer) -> Unit) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Customer) {
            b.customerName.text = item.name.ifBlank { "Customer" }
            b.customerContact.text = listOfNotNull(item.phone, item.email).joinToString(" • ").ifBlank { "Contact unavailable" }
            b.customerStatus.text = item.accountStatus?.backendValue ?: "Status unavailable"
            b.customerOrders.text = item.orderCount?.let { "$it orders" } ?: "Orders unavailable"
            b.root.setOnClickListener { onClick(item) }
        }
    }
    companion object { private val DIFF = object : DiffUtil.ItemCallback<Customer>() { override fun areItemsTheSame(a: Customer,b: Customer)=a.customerId==b.customerId; override fun areContentsTheSame(a: Customer,b: Customer)=a==b } }
}
