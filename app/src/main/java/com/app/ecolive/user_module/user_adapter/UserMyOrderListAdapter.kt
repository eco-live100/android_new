package com.app.ecolive.user_module.user_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.OrderListModel


class UserMyOrderListAdapter(
    var context: Context,
    var dataList: ArrayList<OrderListModel.Item>,
    var onClickListener: ClickListener
) :
    RecyclerView.Adapter<UserMyOrderListAdapter.ViewHolder>() {
    enum class status { pending, confirmed, shipped, delivered, cancelled }
    inner class ViewHolder(itemView: RowMyOrderList2Binding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowMyOrderList2Binding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowMyOrderList2Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_my_order_list2, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.let {
            it.address.text = dataList[position].shippingAddress
            it.qty.text = "Qty : " + dataList[position].products[0].quantity.toString()
            it.productPrice.text = "Amount : " + dataList[position].totalAmount.toString()
            it.productDeliveredDate.text = "Status : " + dataList[position].status.toString()
            it.dateOrderId.text = "Order Id : " + dataList[position].orderNumber.toString()
        }
        if (dataList[position].status.equals( status.pending.name)){
            holder.binding.pending.isChecked =true
            holder.binding.pending.isEnabled =false

        }else   if (dataList[position].status.equals( status.confirmed.name)){
            holder.binding.pending.isChecked =true
            holder.binding.pending.isEnabled =false
            holder.binding.confirmed.isChecked =true
            holder.binding.confirmed.isEnabled =false
        }else   if (dataList[position].status.equals( status.shipped.name)){
            holder.binding.pending.isChecked =true
            holder.binding.pending.isEnabled =false
            holder.binding.confirmed.isChecked =true
            holder.binding.confirmed.isEnabled =false
            holder.binding.shipped.isChecked =true
            holder.binding.shipped.isEnabled =false
            holder.binding.cancelled.isEnabled =false
        }else   if (dataList[position].status.equals( status.delivered.name)){
            holder.binding.pending.isChecked =true
            holder.binding.pending.isEnabled =false
            holder.binding.confirmed.isChecked =true
            holder.binding.confirmed.isEnabled =false
            holder.binding.shipped.isChecked =true
            holder.binding.shipped.isEnabled =false
            holder.binding.delivered.isChecked =true
            holder.binding.delivered.isEnabled =false
            holder.binding.cancelled.isEnabled =false
        }else   if (dataList[position].status.equals( status.cancelled.name)){

            holder.binding.pending.isClickable =false
            holder.binding.pending.isEnabled =false

            holder.binding.confirmed.isClickable =false
            holder.binding.confirmed.isEnabled =false

            holder.binding.shipped.isClickable =false
            holder.binding.shipped.isEnabled =false

            holder.binding.delivered.isClickable =false
            holder.binding.delivered.isEnabled =false
            holder.binding.cancelled.isChecked =true
            holder.binding.cancelled.isClickable =false
            holder.binding.cancelled.isEnabled =false
        }

        holder.binding.confirmed.setOnClickListener {
            onClickListener.onClick(status.confirmed.name,dataList[position]._id)
        }

        holder.binding.shipped.setOnClickListener {
            onClickListener.onClick(status.shipped.name, dataList[position]._id)

        }

        holder.binding.delivered.setOnClickListener {
            onClickListener.onClick(status.delivered.name, dataList[position]._id)

        }

        holder.binding.cancelled.setOnClickListener {
            onClickListener.onClick(status.cancelled.name, dataList[position]._id)

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(data: String, id: String)
    }
}

