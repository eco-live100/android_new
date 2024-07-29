package com.app.ecolive.user_module.user_adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.OrderListModel
import com.app.ecolive.utils.AppConstant
import com.bumptech.glide.Glide


class UserMyOrderList2Adapter(var context: Context, var dataList: ArrayList<OrderListModel.Item>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<UserMyOrderList2Adapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowMyOrderListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowMyOrderListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowMyOrderListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_my_order_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.let {
            it.productName.text = dataList[position].products[0].productId.name
            it.address.text =dataList[position].shippingAddress
            it.qty.text ="Qty : "+dataList[position].products[0].quantity.toString()
            it.productPrice.text ="Amount : "+dataList[position].totalAmount.toString()
            it.productDeliveredDate.text ="Status : "+dataList[position].status.toString()
            it.dateOrderId.text ="Order Id : "+dataList[position].orderNumber.toString()
            var url = AppConstant.product_listUrl+dataList[position].products[0].productId.images[0].name
            Log.d("TAG", "onBindViewHolder: "+url)
            Glide.with(context).load(url)
                .placeholder(R.drawable.ic_login)
                .into(it.productImage)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

