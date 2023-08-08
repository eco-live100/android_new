package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeCategoryListBinding
import com.app.ecolive.databinding.RowHomeProductListBinding
import com.app.ecolive.localmodel.HomeCategoryListModel
import com.app.ecolive.localmodel.HomeProductListModel
import com.bumptech.glide.Glide


class HomeProductListAdapter(var context: Context, var dataList: ArrayList<HomeProductListModel>,var onClickListener: ClickListener) :
    RecyclerView.Adapter<HomeProductListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowHomeProductListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowHomeProductListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowHomeProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_home_product_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.homepageProductName.text=dataList[position].name
        holder.binding.homepageProductShopLivePrice.text=dataList[position].shopLivePrice
        holder.binding.homepageProductShopOnlinePrice.text=dataList[position].shopOnlinePrice
//        holder.binding.homepageProductImage.setImageDrawable(dataList[position].image)
        holder.binding.constraintHomepageProductList.setOnClickListener { onClickListener.viewProductDetails(dataList[position].productId) }
        Glide.with(context).load(dataList[position].imageUrl).into(holder.binding.homepageProductImage)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    interface ClickListener {
        fun viewProductDetails(pos: String)
    }
}