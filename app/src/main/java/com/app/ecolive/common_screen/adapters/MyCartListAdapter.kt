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
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.MyCartListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.utils.Utils.Companion.priceMultiplyByQty
import com.bumptech.glide.Glide
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class MyCartListAdapter(var context: Context, var dataList: ArrayList<MyCartListModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<MyCartListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowMyCartBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowMyCartBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowMyCartBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_my_cart, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.myCartProductName.text=dataList[position].productName
        holder.binding.myCartProductPrice.text="$"+dataList[position].shopOnlinePrice
        holder.binding.myCartQty.text=dataList[position].selectQty
        Glide.with(context).load(dataList[position].image).into(holder.binding.myCartProductImage)
        holder.binding.viewProductDetails.setOnClickListener {
            onClickListener.onClick(position)
        }

        holder.binding.deleteButton.setOnClickListener {
            onClickListener.onDelete(position)
        }

        holder.binding.myCartQtyMinus.setOnClickListener {
            onClickListener.onMinus(position)
        }
        holder.binding.myCartQtyMinus.setOnClickListener {
            onClickListener.onPlus(position)
        }
        if (dataList[position].selectQty=="1")
        {
            holder.binding.myCartProductPrice2.visibility=View.GONE
        }else{
            holder.binding.myCartProductPrice2.visibility=View.VISIBLE
         //   holder.binding.myCartProductPrice2.text="= $"+priceMultiplyByQty(dataList[position].shopOnlinePrice,holder.binding.myCartQty.text.toString())

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
        fun onDelete(pos: Int)
        fun onMinus(pos: Int)
        fun onPlus(pos: Int)
    }
}

