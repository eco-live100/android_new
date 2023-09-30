package com.app.ecolive.taximodule.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
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

        var item = dataList[position]
        if(item.bookingStatus=="requested"){
            holder.binding.bookingStatusTv.text="${item.bookingStatus}".capitalize()
            holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_red))
        }else{
            holder.binding.bookingStatusTv.text="${item.bookingStatus}".capitalize()
            holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_51E555))
        }

        holder.binding.bookingIdTv.text="Booking Id:- ${item.bookingNumber}"
        holder.binding.bookingDateTv.text="${item.createdAt}"
        holder.binding.fromAddressTv.text="From Address:- ${item.userAddress}"
        holder.binding.toAddressTv.text="To Address:- ${item.driverAddress}"
        holder.binding.tvTotBill.text="Total Bill:- $ ${item.amount}"
        //holder.binding.toAddressTv.setImageDrawable(dataList[position].image)
        holder.binding.tvTrackOrder.setOnClickListener { onClickListener.onClick(position) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

