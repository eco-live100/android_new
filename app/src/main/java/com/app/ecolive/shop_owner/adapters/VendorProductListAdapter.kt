package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowVendorProductListBinding
import com.app.ecolive.localmodel.SimilarProductListModel


class VendorProductListAdapter(var context: Context, var dataList: ArrayList<SimilarProductListModel>) :
    RecyclerView.Adapter<VendorProductListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowVendorProductListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowVendorProductListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowVendorProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_vendor_product_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.productName.text=dataList[position].name
        holder.binding.productDescription.text=dataList[position].subCatogary
        holder.binding.productPrice.text=dataList[position].shopOnlinePrice
        holder.binding.productImage.setImageDrawable(dataList[position].image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
        fun onMenuClick(it: View, position: Int)
    }
}

