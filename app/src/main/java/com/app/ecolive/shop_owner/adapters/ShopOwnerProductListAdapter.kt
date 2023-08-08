package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.localmodel.SimilarProductListModel


class ShopOwnerProductListAdapter(var context: Context, var dataList: ArrayList<SimilarProductListModel>,var onClickListener: ClickListener) :
    RecyclerView.Adapter<ShopOwnerProductListAdapter.ViewHolder>() {

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
        holder.binding.productCategory.text=dataList[position].subCatogary
        holder.binding.productName.text=dataList[position].name
        holder.binding.imageViewMenu.visibility=View.VISIBLE
        holder.binding.imageViewMenu.setOnClickListener { onClickListener.onMenuClick(it,position) }
        holder.binding.productShopOnlinePrice.text=dataList[position].shopOnlinePrice
        holder.binding.homepageProductImage.setImageDrawable(dataList[position].image)
        holder.binding.viewProductDetails.setOnClickListener {
            onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
        fun onMenuClick(it: View, position: Int)
    }
}

