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
import com.app.ecolive.utils.PreferenceKeeper
import com.bumptech.glide.Glide
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.Conversation


class ChatAdapter(var context: Context, var dataList: List<BaseMessage?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var SENDER_VIEW = 1
    var RECIVER_VIEW = 2

    inner class ViewHolder(itemView: RowSenderChatBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowSenderChatBinding = itemView

    }

    inner class ViewHolder2(itemView: RowReciverChatBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowReciverChatBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =

        when (viewType) {
            SENDER_VIEW -> {
                val binding: RowSenderChatBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_sender_chat, parent, false
                )
                ViewHolder(binding)
            }
            else -> {
                val binding: RowReciverChatBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_reciver_chat, parent, false
                )
                ViewHolder2(binding)
            }


        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ViewHolder -> {
                if(!dataList[position]?.type.equals("video")){
                    holder.binding.message.text =dataList[position]!!.rawMessage.getJSONObject("data").getString("text")

                }
            }
            is ViewHolder2 ->{
                if(!dataList[position]?.type.equals("video")) {
                    holder.binding.message.text =
                        dataList[position]!!.rawMessage.getJSONObject("data").getString("text")

                }
            }
        }
        // Glide.with(context).load(Uri.parse(dataList[position].image)).placeholder(R.drawable.user_icon_white).into(holder.binding.chatImage)


    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position]!!.receiverUid == PreferenceKeeper.instance.loginResponse!!._id) RECIVER_VIEW else SENDER_VIEW
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(id: String)
    }
}

