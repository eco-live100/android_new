package com.app.ecolive.rider_module.adapter
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.AcceptDeclineOrderListModel
import com.app.ecolive.localmodel.CurrentOrderListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class AcceptDeclineOrderListAdapter(var context: Context, var dataList: ArrayList<AcceptDeclineOrderListModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<AcceptDeclineOrderListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowAcceptedDeclineOrderListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowAcceptedDeclineOrderListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowAcceptedDeclineOrderListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_accepted_decline_order_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.acceptedDeclineOrderId.text=dataList[position].orderId
        holder.binding.acceptedDeclineOrderDate.text=dataList[position].orderDate
        holder.binding.acceptedDeclineOrderTime.text=dataList[position].orderTime
        holder.binding.acceptedDeclineOrderTitle.text=dataList[position].orderName
        holder.binding.acceptedDeclineOrderItem1.text=dataList[position].orderItem1
        holder.binding.acceptedDeclineOrderItemPrice1.text=dataList[position].orderItemPrice1
        holder.binding.acceptedDeclineOrderItem2.text=dataList[position].orderItem2
        holder.binding.acceptedDeclineOrderItemPrice2.text=dataList[position].orderItemPrice2
        holder.binding.acceptedDeclineOrderTotalAmount.text=dataList[position].orderTotal
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

