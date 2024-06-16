package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeCategoryWithoutImageListBinding
import com.app.ecolive.databinding.RowProductDetailColorVariationBinding
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.databinding.RowShopProductListBinding
import com.app.ecolive.databinding.RowSimilarProductLsitBinding
import com.app.ecolive.localmodel.SimilarProductListModel
import com.bumptech.glide.Glide
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class ShopProductListAdapter(var context: Context, var dataList: ArrayList<SimilarProductListModel>, var isShowPrecription:Boolean, var onClickListener: ClickListener) :
    RecyclerView.Adapter<ShopProductListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowShopProductListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowShopProductListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowShopProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_shop_product_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.binding.productName.text=dataList[position].name
        holder.binding.productShopOnlinePrice.text="Online price"+dataList[position].shopOnlinePrice
        holder.binding.productShopLivePrice.text="Live price"+dataList[position].shopLivePrice
        holder.binding.homepageProductImage.setImageDrawable(dataList[position].image)
         Glide.with(context).load(dataList[position].imageUrl).placeholder(R.drawable.pharmacy_catogary_icon).into(holder.binding.homepageProductImage)

        holder.binding.viewProductDetails.setOnClickListener {
            onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

