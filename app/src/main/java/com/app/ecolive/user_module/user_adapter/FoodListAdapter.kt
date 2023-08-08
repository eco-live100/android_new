package com.app.ecolive.user_module.user_adapter
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
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class FoodListAdapter(var context: Context, var dataList: ArrayList<SimilarProductListModel>, var isFromGrocery:Boolean,var onClickListener: ClickListener) :
    RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RawFoodlistBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RawFoodlistBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RawFoodlistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.raw_foodlist, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.productDeliveredDate.text=dataList[position].deliveredDate
//        holder.binding.productName.text=dataList[position].productName
//        holder.binding.productPrice.text=dataList[position].amount
//        holder.binding.productRating.text=dataList[position].rating
        if(isFromGrocery){
        holder.binding.foodImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_grocery))}
        holder.binding.constraintNext.setOnClickListener { onClickListener.onClick(position) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

