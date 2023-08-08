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
import com.app.ecolive.localmodel.CurrentOrderListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class CurrentOrderListAdapter(var context: Context, var dataList: ArrayList<CurrentOrderListModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<CurrentOrderListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowCurrentListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowCurrentListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowCurrentListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_current_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.CurrentOrderUserName.text=dataList[position].currentOrderUserName
        holder.binding.CurrentOrderDate.text=dataList[position].currentOrderDate
        holder.binding.CurrentOrderTime.text=dataList[position].currentOrderTime
        holder.binding.CurrentDeliveryEstimateTime.text=dataList[position].currentDeliveryEstimateTime
        holder.binding.CurrentOrderEarning.text=dataList[position].currentOrderEarning
        holder.binding.CurrentOrderPaymentMode.text=dataList[position].currentOrderPaymentMode
        holder.binding.CurrentOrderProfileImage.setImageDrawable(dataList[position].image)
        holder.binding.btnAccept.setOnClickListener { onClickListener.onAccept(position) }
        holder.binding.btnDecline.setOnClickListener { onClickListener.onDecline(position) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onAccept(pos: Int)
        fun onDecline(pos: Int)
    }
}

