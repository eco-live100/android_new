package com.app.ecolive.shop_owner.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowShopProductListBinding
import com.app.ecolive.shop_owner.model.Item
import com.app.ecolive.shop_owner.model.ProductOutofStockModel
import com.app.ecolive.utils.AppConstant
import com.bumptech.glide.Glide


class ShopProductListAdapter(
    var context: Context,
    var dataList: ArrayList<Item>,
    var onClickListener: ClickListener
) :
    RecyclerView.Adapter<ShopProductListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: RowShopProductListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowShopProductListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowShopProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_shop_product_list, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.productName.text = dataList[position].name
        holder.binding.productShopOnlinePrice.text = "Online price ${dataList[position].price}"
        holder.binding.productShopLivePrice.text = "Live price ${dataList[position].livePrice}"
        holder.binding.switchOutofStock.isChecked = dataList[position].outofstock
        var url =AppConstant.product_listUrl+dataList[position].images[0].name
        Log.d("TAG", "onBindViewHolder: "+url)
        Glide.with(context).load(url)
            .placeholder(R.drawable.ic_login)
            .into(holder.binding.homepageProductImage)

        holder.binding.switchOutofStock.setOnClickListener {
            onClickListener.onOutofStock(dataList[position])
        }

        holder.binding.deleteProduct.setOnClickListener {
            onClickListener.onProductDelete(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun update(items: java.util.ArrayList<Item>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    fun updateOutOfStock(product: ProductOutofStockModel.Product) {
         for (i in dataList){
             if(i._id ==product._id){
                 i.outofstock =product.outofstock
             }
         }
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onOutofStock(data: Item)
        fun onProductDelete(data: Item)

    }
}

