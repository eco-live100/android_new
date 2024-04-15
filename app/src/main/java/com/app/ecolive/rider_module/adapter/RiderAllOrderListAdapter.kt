package com.app.ecolive.rider_module.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RiderOrderListItemBinding
import com.app.ecolive.pharmacy_module.model.GetAllReadyOrdersForDriverData


class RiderAllOrderListAdapter(var context: Context, var dataList: List<GetAllReadyOrdersForDriverData>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<RiderAllOrderListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RiderOrderListItemBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RiderOrderListItemBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RiderOrderListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rider_order_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataList[position]
        when (item.status) {
            8 -> {
                holder.binding.tvTotBill.visibility = View.GONE
               // holder.binding.tvTrackOrder.visibility = View.GONE
                holder.binding.bookingStatusTv.text="Completed".capitalize()
                holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_red))
            }
            5 -> {
                holder.binding.tvTotBill.visibility = View.VISIBLE
               // holder.binding.tvTrackOrder.visibility = View.VISIBLE
                holder.binding.bookingStatusTv.text="Ready For Dispatch By Pharmacy".capitalize()
                holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_FF9100))
            }
            6 -> {
                holder.binding.tvTotBill.visibility = View.VISIBLE
                // holder.binding.tvTrackOrder.visibility = View.VISIBLE
                holder.binding.bookingStatusTv.text="Driver Accept".capitalize()
                holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_006400))
            }
            7 -> {
                holder.binding.tvTotBill.visibility = View.VISIBLE
                // holder.binding.tvTrackOrder.visibility = View.VISIBLE
                holder.binding.bookingStatusTv.text="Driver Pickup".capitalize()
                holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_006400))
            }
            else -> {
                holder.binding.tvTotBill.visibility = View.GONE
               // holder.binding.tvTrackOrder.visibility = View.GONE
            }
        }
        holder.binding.bookingIdTv.text="Booking Id:- ${item._id}"
        holder.binding.bookingDateTv.text="${item.createdAt}"
        holder.binding.fromAddressTv.text="${item.pharmacy.location}"
       // holder.binding.toAddressTv.text="${item.toAddress}"
       // holder.binding.tvTotBill.text="Total Bill:- $ ${item.amount}"
        //holder.binding.toAddressTv.setImageDrawable(dataList[position].image)
        holder.itemView.setOnClickListener {
                onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

