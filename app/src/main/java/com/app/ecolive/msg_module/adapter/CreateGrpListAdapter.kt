package com.app.ecolive.msg_module.adapter
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.UserListModel
import com.bumptech.glide.Glide


class CreateGrpListAdapter(var context: Context, var dataList: ArrayList<UserListModel>, var clickListern:ClickListener) :
    RecyclerView.Adapter<CreateGrpListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowCreategrpBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowCreategrpBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowCreategrpBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_creategrp, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.chatTitle.text=dataList[position].username
        Glide.with(context).load(Uri.parse(dataList[position].image)).placeholder(R.drawable.user_icon_white).into(holder.binding.chatImage)

        holder.binding.root.setOnClickListener {
            clickListern.onClick(dataList[position].id)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(id: String)
    }
}

