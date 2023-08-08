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
import com.app.ecolive.user_module.model.AddressModel
 import com.app.ecolive.user_module.model.UserModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class MyAddressAdapter(var context: Context,  var onClickListener: ClickListener) :
    RecyclerView.Adapter<MyAddressAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowMyaddressBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowMyaddressBinding = itemView

    }
    var dataList= ArrayList<AddressModel.Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowMyaddressBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_myaddress, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.address.text = dataList[position].title
        holder.binding.addressName.text = dataList[position].fullName
        holder.binding.mobile.text = dataList[position].mobile
        holder.binding.addressTitle.text = dataList[position].addressType
        holder.binding.Delete.setOnClickListener { onClickListener.onClick(dataList[position]._id) }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: String)
    }

    fun addData(data:ArrayList<AddressModel.Data>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }
}

