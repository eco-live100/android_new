package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.content.Intent
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
import com.app.ecolive.shop_owner.FoodItemDetailActivity
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class FoodItemAdapter(var context: Context,  var onClickListener: ClickListener) :
    RecyclerView.Adapter<FoodItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowFoodItemBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowFoodItemBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowFoodItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_food_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
        holder.itemView.setOnClickListener { context.startActivity(Intent(context,FoodItemDetailActivity::class.java)) }
    }

    override fun getItemCount(): Int {
        return 10
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

