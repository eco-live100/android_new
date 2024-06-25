package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.payment_module.model.Contact
import com.app.ecolive.shop_owner.model.ProductModel
import com.app.ecolive.utils.AppConstant
import com.bumptech.glide.Glide
import java.util.Locale


class ProductListAdapter(
    var context: Context,
    var dataList: ArrayList<ProductModel.Doc>,
    var productFilterList: ArrayList<ProductModel.Doc>,
    var isShowPrecription: Boolean,
    var onClickListener: ClickListener
) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>(),Filterable {

    inner class ViewHolder(itemView: RowProductListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowProductListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowProductListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_product_list, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.productName.text = productFilterList[position].name
        holder.binding.productShopLivePrice.text = productFilterList[position].livePrice.toString()
        holder.binding.productShopOnlinePrice.text = productFilterList[position].price.toString()
        holder.binding.imageViewMenu.visibility = View.GONE
        Glide.with(context).load(productFilterList[position].images[0])
            .placeholder(R.drawable.appicon_512).into(holder.binding.homepageProductImage)

        holder.binding.viewProductDetails.setOnClickListener {
            onClickListener.onClick(productFilterList[position]._id)
        }
    }

    override fun getItemCount(): Int {
        return productFilterList.size
    }

    fun update(docs: java.util.ArrayList<ProductModel.Doc>) {

        this.dataList = docs
        this.productFilterList = docs
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onClick(id: String)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                productFilterList = if (charString.isEmpty()) {
                    dataList
                } else {
                    val filteredList: ArrayList<ProductModel.Doc> = ArrayList()
                    for (contactItem in dataList) {
                        if (contactItem.name?.toLowerCase()
                                ?.contains(charString.lowercase(Locale.getDefault()))
                            == true
                        ) {
                            filteredList.add(contactItem)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = productFilterList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                productFilterList = (filterResults.values as? ArrayList<ProductModel.Doc>)!!
                notifyDataSetChanged()
            }
        }
    }
}

