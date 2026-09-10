package com.daily.nexamartpartner.features.admin.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.databinding.ItemOrderSummaryCardBinding
import com.daily.nexamartpartner.features.admin.domain.model.RecentOrderSummary

class OrderSummaryAdapter : ListAdapter<RecentOrderSummary, OrderSummaryAdapter.OrderSummaryViewHolder>(
    DiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val binding = ItemOrderSummaryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderSummaryViewHolder(
        private val binding: ItemOrderSummaryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentOrderSummary) {
            binding.orderIdText.text = item.orderId
            binding.customerNameText.text = item.customerName.ifBlank {
                binding.root.context.getString(R.string.admin_dashboard_unknown_customer)
            }
            binding.orderAmountText.text = item.amount?.let { amount ->
                ValueFormatter.formatCurrency(amount, item.currencyCode)
            } ?: binding.root.context.getString(R.string.admin_dashboard_amount_unavailable)
            binding.orderStatusText.text = item.status
            binding.orderTimeText.text = item.createdAt ?: binding.root.context.getString(
                R.string.admin_dashboard_time_unavailable
            )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RecentOrderSummary>() {
        override fun areItemsTheSame(oldItem: RecentOrderSummary, newItem: RecentOrderSummary): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: RecentOrderSummary, newItem: RecentOrderSummary): Boolean {
            return oldItem == newItem
        }
    }
}
