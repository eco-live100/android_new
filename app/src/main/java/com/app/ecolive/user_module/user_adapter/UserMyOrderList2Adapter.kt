package com.app.ecolive.user_module.user_adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.MyOrderListModel


class UserMyOrderList2Adapter(var context: Context, var dataList: ArrayList<MyOrderListModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<UserMyOrderList2Adapter.ViewHolder>() {

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
        if(position%2==0){
            holder.binding.bookingStatusTv.text="Received"
            holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_51E555))
        }else{
            holder.binding.bookingStatusTv.text="Cancelled"
            holder.binding.bookingStatusTv.setTextColor(context.resources.getColor(R.color.color_red))
        }

//        holder.binding.productName.text=dataList[position].productName
//        holder.binding.productPrice.text=dataList[position].amount
//        holder.binding.productRating.text=dataList[position].rating
//        holder.binding.productImage.setImageDrawable(dataList[position].image)
        holder.binding.tvTrackOrder.setOnClickListener { onClickListener.onClick(position) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

