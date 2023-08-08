package com.app.ecolive.user_module.user_adapter
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
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class UserMyOrderList2Adapter(var context: Context, var dataList: ArrayList<MyOrderListModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<UserMyOrderList2Adapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowMyOrderList2Binding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowMyOrderList2Binding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowMyOrderList2Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_my_order_list2, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position%2==0){
            holder.binding.tvIsReceived.text="Received"
            holder.binding.tvIsReceived.setTextColor(context.resources.getColor(R.color.color_51E555))
        }else{
            holder.binding.tvIsReceived.text="Cancelled"
            holder.binding.tvIsReceived.setTextColor(context.resources.getColor(R.color.color_red))
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

