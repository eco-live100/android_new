package com.app.ecolive.common_screen.adapters
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeCategoryWithoutImageListBinding
import com.app.ecolive.databinding.RowProductDetailColorVariationBinding
import com.app.ecolive.databinding.RowSimilarProductLsitBinding
import com.app.ecolive.localmodel.SimilarProductListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class ProductDetailSimilarProductAdapter(var context: Context, var dataList: ArrayList<SimilarProductListModel>) :
    RecyclerView.Adapter<ProductDetailSimilarProductAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowSimilarProductLsitBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowSimilarProductLsitBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowSimilarProductLsitBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_similar_product_lsit, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.productCategory.text=dataList[position].subCatogary
        holder.binding.productName.text=dataList[position].name
        holder.binding.productShopOnlinePrice.text=dataList[position].shopOnlinePrice
        holder.binding.homepageProductImage.setImageDrawable(dataList[position].image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

