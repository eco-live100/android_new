package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeProductListBinding
import com.app.ecolive.shop_owner.model.ProductModel
import com.bumptech.glide.Glide


class HomeProductListAdapter(
    var context: Context, var dataList: ArrayList<ProductModel.Doc>,
    var onClickListener: ClickListener) :
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
        holder.binding.homepageProductShopLivePrice.text=dataList[position].livePrice.toString()
        holder.binding.homepageProductShopOnlinePrice.text=dataList[position].price.toString()
        holder.binding.homepageProductShipping.text= "Shipping ${dataList[position].fastDeliver}"
//        holder.binding.homepageProductImage.setImageDrawable(dataList[position].image)
        holder.binding.constraintHomepageProductList.setOnClickListener { onClickListener.viewProductDetails(dataList[position]._id) }
        Glide.with(context).load(dataList[position].images[0]).placeholder(R.drawable.app_logo_bgtrans).into(holder.binding.homepageProductImage)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    interface ClickListener {
        fun viewProductDetails(pos: String)
    }
}