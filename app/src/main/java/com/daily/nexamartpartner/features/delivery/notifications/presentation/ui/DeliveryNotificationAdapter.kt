package com.daily.nexamartpartner.features.delivery.notifications.presentation.ui
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.daily.nexamartpartner.databinding.ItemDeliveryNotificationBinding
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotification
class DeliveryNotificationAdapter(private val onClick:(DeliveryNotification)->Unit):RecyclerView.Adapter<DeliveryNotificationAdapter.VH>(){private var items=emptyList<DeliveryNotification>();fun submitList(v:List<DeliveryNotification>){items=v;notifyDataSetChanged()};override fun onCreateViewHolder(p:ViewGroup,v:Int)=VH(ItemDeliveryNotificationBinding.inflate(LayoutInflater.from(p.context),p,false));override fun getItemCount()=items.size;override fun onBindViewHolder(h:VH,i:Int)=h.bind(items[i]);inner class VH(private val b:ItemDeliveryNotificationBinding):RecyclerView.ViewHolder(b.root){fun bind(n:DeliveryNotification){b.titleText.text=n.title.ifBlank{"NexaMart update"};b.messageText.text=n.message;b.dateText.text=n.createdAt.orEmpty();b.unreadText.visibility=if(n.read)View.GONE else View.VISIBLE;b.root.setOnClickListener{onClick(n)}}}}
