package com.app.ecolive.shop_owner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.localmodel.AddProductImageListModel

class AddProductImageAdapter(context: Context, list: ArrayList<AddProductImageListModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    private val context: Context = context
    var list: ArrayList<AddProductImageListModel> = list

    private inner class View1ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
        }
    }

    private inner class View2ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        fun bind(position: Int) {
            val recyclerViewModel = list[position]
           imageView.setImageDrawable(recyclerViewModel.addImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            return View1ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_list_add_product_image_view_one, parent, false)
            )
        }
        return View2ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_list_add_product_image_view_two, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].viewType === VIEW_TYPE_ONE) {
            (holder as View1ViewHolder).bind(position)
        } else {
            (holder as View2ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }
}