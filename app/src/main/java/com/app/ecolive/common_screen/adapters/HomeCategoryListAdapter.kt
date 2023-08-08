package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeCategoryListBinding
import com.app.ecolive.localmodel.HomeCategoryListModel
import com.app.ecolive.shop_owner.model.ShopCategryListModel


class HomeCategoryListAdapter(var context: Context, var dataList:
ArrayList<ShopCategryListModel.Data>,var onClickListener: ClickListener) :
    RecyclerView.Adapter<HomeCategoryListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowHomeCategoryListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowHomeCategoryListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowHomeCategoryListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_home_category_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTitle.text=dataList[position].categoryName
        holder.binding.imageView15.setImageDrawable(context.resources.getDrawable(R.drawable.category_image1))
        holder.binding.viewProductList.setOnClickListener {
            onClickListener.viewProductList(position)
        }
        if(position==0){
            holder.binding.imageView15.setImageDrawable(context.resources.getDrawable(R.drawable.taxi_catogary_icon))
        }else if(position==1){
            holder.binding.imageView15.setImageDrawable(context.resources.getDrawable(R.drawable.food_catogary_icon))

        } else if(position==2){
            holder.binding.imageView15.setImageDrawable(context.resources.getDrawable(R.drawable.groccery_catogry_icon))

        } else if(position==3){
            holder.binding.imageView15.setImageDrawable(context.resources.getDrawable(R.drawable.pharmacy_catogary_icon))

        }else if(position==4){
            holder.binding.imageView15.setImageDrawable(context.resources.getDrawable(R.drawable.retail_catogary_icon))

        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    interface ClickListener {
        fun viewProductList(pos: Int)
    }
}