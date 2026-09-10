package com.daily.nexamartpartner.features.admin.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.databinding.ItemAdminOrderSummaryBinding
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSummary

class AdminOrdersAdapter(
    private val onClick: (AdminOrderSummary) -> Unit
) : ListAdapter<AdminOrderSummary, AdminOrdersAdapter.AdminOrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val binding = ItemAdminOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminOrderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AdminOrderViewHolder(
        private val binding: ItemAdminOrderSummaryBinding,
        private val onClick: (AdminOrderSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AdminOrderSummary) {
            binding.orderIdText.text = item.orderId
            binding.customerNameText.text = item.customerName.ifBlank {
                binding.root.context.getString(R.string.admin_dashboard_unknown_customer)
            }
            val amountText = item.totalAmount?.let {
                ValueFormatter.formatCurrency(it, item.currencyCode)
            } ?: binding.root.context.getString(R.string.admin_dashboard_amount_unavailable)
            val items = item.itemCount?.toString() ?: binding.root.context.getString(R.string.admin_orders_count_unavailable)
            val createdAt = item.createdAt ?: binding.root.context.getString(R.string.admin_dashboard_time_unavailable)
            binding.orderMetaText.text = binding.root.context.getString(
                R.string.admin_orders_meta_template,
                items,
                amountText,
                createdAt
            )
            binding.statusText.text = item.orderStatus.backendValue
            binding.paymentStatusText.text = item.paymentStatus.backendValue
            binding.root.setOnClickListener { onClick(item) }
            binding.root.contentDescription = binding.root.context.getString(
                R.string.cd_order_summary_card,
                item.orderId,
                item.orderStatus.backendValue
            )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<AdminOrderSummary>() {
        override fun areItemsTheSame(oldItem: AdminOrderSummary, newItem: AdminOrderSummary): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: AdminOrderSummary, newItem: AdminOrderSummary): Boolean {
            return oldItem == newItem
        }
    }
}
