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
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class ProductDetailColorVariationAdapter(var context: Context, var dataList: ArrayList<DrawerCategoryListModel>) :
    RecyclerView.Adapter<ProductDetailColorVariationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowProductDetailColorVariationBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowProductDetailColorVariationBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowProductDetailColorVariationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_product_detail_color_variation, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.cardViewColor.setCardBackgroundColor(Color.parseColor(dataList[position].title))
        holder.binding.cardViewSelected.setCardBackgroundColor(Color.parseColor(dataList[position].title))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

