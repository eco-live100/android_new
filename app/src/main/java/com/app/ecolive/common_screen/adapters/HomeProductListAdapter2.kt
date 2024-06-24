package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeProductListBinding
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.shop_owner.model.ProductModel
import com.bumptech.glide.Glide


class HomeProductListAdapter2(
    var context: Context, var dataList: ArrayList<ProductModel.Doc>,
    var onClickListener: ClickListener) :
    RecyclerView.Adapter<HomeProductListAdapter2.ViewHolder>() {

    inner class ViewHolder(itemView : RowProductListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowProductListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_product_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.productName.text=dataList[position].name
        holder.binding.productShopLivePrice.text="Shop live "+dataList[position].livePrice.toString()
        holder.binding.productShopOnlinePrice.text="Shop online "+dataList[position].price.toString()
      //  holder.binding.homepageProductShipping.text= "Shipping ${dataList[position].fastDeliver}"
//        holder.binding.homepageProductImage.setImageDrawable(dataList[position].image)
        holder.binding.viewProductDetails.setOnClickListener { onClickListener.viewProductDetails(dataList[position]._id) }
        Glide.with(context).load(dataList[position].images[0]).placeholder(R.drawable.app_logo_bgtrans).into(holder.binding.homepageProductImage)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    interface ClickListener {
        fun viewProductDetails(pos: String)
    }
}