package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowHomeCategoryWithoutImageListBinding
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class DrawerCategoryListAdapter(var context: Context, var dataList: ArrayList<DrawerCategoryListModel>) :
    RecyclerView.Adapter<DrawerCategoryListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowHomeCategoryWithoutImageListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowHomeCategoryWithoutImageListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowHomeCategoryWithoutImageListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_home_category_without_image_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.homepageCategoryName.text=dataList[position].title
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}