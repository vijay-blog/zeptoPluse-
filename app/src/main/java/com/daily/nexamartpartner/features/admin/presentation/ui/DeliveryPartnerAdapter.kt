package com.daily.nexamartpartner.features.admin.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.ItemDeliveryPartnerBinding
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerSummary

class DeliveryPartnerAdapter(
    private val onClick: (DeliveryPartnerSummary) -> Unit
) : ListAdapter<DeliveryPartnerSummary, DeliveryPartnerAdapter.Holder>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(
            ItemDeliveryPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick
        )

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    class Holder(
        private val binding: ItemDeliveryPartnerBinding,
        private val onClick: (DeliveryPartnerSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeliveryPartnerSummary) {
            binding.avatarText.text = initials(item.name)
            binding.partnerNameText.text = item.name.ifBlank {
                binding.root.context.getString(R.string.delivery_partner_name_unavailable)
            }
            binding.partnerPhoneText.text = item.phone
                ?: binding.root.context.getString(R.string.delivery_partner_value_unavailable)
            binding.partnerStatusesText.text = binding.root.context.getString(
                R.string.delivery_partner_status_line,
                item.accountStatus.backendValue,
                item.verificationStatus.backendValue,
                item.availability.backendValue
            )
            binding.activeDeliveriesText.text = binding.root.context.getString(
                R.string.delivery_partner_active_deliveries,
                item.activeDeliveries?.toString()
                    ?: binding.root.context.getString(R.string.admin_dashboard_value_unavailable)
            )
            binding.root.contentDescription = binding.root.context.getString(
                R.string.cd_delivery_partner_card,
                item.name,
                item.accountStatus.backendValue,
                item.availability.backendValue
            )
            binding.root.setOnClickListener { onClick(item) }
        }

        private fun initials(name: String): String =
            name.trim().split(Regex("\\s+")).filter(String::isNotBlank).take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifBlank { "DP" }
    }

    private object Diff : DiffUtil.ItemCallback<DeliveryPartnerSummary>() {
        override fun areItemsTheSame(oldItem: DeliveryPartnerSummary, newItem: DeliveryPartnerSummary) =
            oldItem.partnerId == newItem.partnerId

        override fun areContentsTheSame(oldItem: DeliveryPartnerSummary, newItem: DeliveryPartnerSummary) =
            oldItem == newItem
    }
}
