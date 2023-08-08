package com.app.ecolive.msg_module.adapter
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
import com.app.ecolive.localmodel.ChatListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.localmodel.TransactionHistoryListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class FriendListAdapter(var context: Context,   var clickListern:ClickListener) :
    RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowFriendlistBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowFriendlistBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowFriendlistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_friendlist, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

    interface ClickListener {
        fun onClick(pos: Int)

    }
}

