package com.daily.nexamartpartner.features.delivery.earnings.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.databinding.ItemDeliveryEarningBinding
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningEntry

class DeliveryEarningAdapter : RecyclerView.Adapter<DeliveryEarningAdapter.VH>() {
    private val items = mutableListOf<DeliveryEarningEntry>()
    fun submitList(value: List<DeliveryEarningEntry>) { items.clear(); items.addAll(value); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemDeliveryEarningBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
    class VH(private val b: ItemDeliveryEarningBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: DeliveryEarningEntry) {
            b.orderId.text = item.orderId?.takeIf { it.isNotBlank() } ?: "Earning"
            b.amount.text = item.amount?.let { ValueFormatter.formatCurrency(it, item.currencyCode) } ?: "Unavailable"
            b.description.text = item.description.orEmpty().ifBlank { "Delivery earning" }
            b.meta.text = listOfNotNull(item.earnedAt?.takeIf { it.isNotBlank() }, item.status?.takeIf { it.isNotBlank() }).joinToString(" · ").ifBlank { "Unavailable" }
        }
    }
}
