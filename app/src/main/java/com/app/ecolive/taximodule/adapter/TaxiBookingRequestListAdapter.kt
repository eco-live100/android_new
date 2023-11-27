package com.app.ecolive.taximodule.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.TaxiBookingRequestiListItemBinding
import com.app.ecolive.taximodule.model.TaxiBookingRequestList


class TaxiBookingRequestListAdapter(var context: Context, var dataList: List<TaxiBookingRequestList.Data>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<TaxiBookingRequestListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : TaxiBookingRequestiListItemBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : TaxiBookingRequestiListItemBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: TaxiBookingRequestiListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.taxi_booking_requesti_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataList[position]
        when (item.bookingStatus) {
            "requested" -> {
                holder.binding.tvTotBill.visibility = View.GONE
                holder.binding.tvTrackOrder.visibility = View.GONE
                holder.binding.bookingStatusTv.text="${item.bookingStatus}".capitalize()
                holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_red))
            }
            "accepted" -> {
                holder.binding.tvTotBill.visibility = View.VISIBLE
                holder.binding.tvTrackOrder.visibility = View.VISIBLE
                holder.binding.bookingStatusTv.text="${item.bookingStatus}".capitalize()
                holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_006400))
            }
            else -> {
                holder.binding.tvTotBill.visibility = View.GONE
                holder.binding.tvTrackOrder.visibility = View.GONE
            }
        }
        holder.binding.bookingIdTv.text="Booking Id:- ${item.bookingNumber}"
        holder.binding.bookingDateTv.text="${item.createdAt}"
        holder.binding.fromAddressTv.text="${item.fromAddress}"
        holder.binding.toAddressTv.text="${item.toAddress}"
        holder.binding.tvTotBill.text="Total Bill:- $ ${item.amount}"
        //holder.binding.toAddressTv.setImageDrawable(dataList[position].image)
        holder.binding.tvTrackOrder.setOnClickListener {
            if(item.bookingStatus=="accepted"){
                onClickListener.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

