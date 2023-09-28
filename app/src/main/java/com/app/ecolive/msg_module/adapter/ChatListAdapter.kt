package com.app.ecolive.msg_module.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.bumptech.glide.Glide
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.models.User


class ChatListAdapter(var context: Context, var dataList: ArrayList<Conversation>, var clickListern:ClickListener) :
    RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowChatlistBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowChatlistBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowChatlistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_chatlist, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.chatTitle.text=  (dataList[position].conversationWith as User).name?:""
        if(dataList[position].lastMessage.type.equals("video")||dataList[position].lastMessage.type.equals("audio")){
            holder.binding.chatSubTitle.text=""

        }else{
            holder.binding.chatSubTitle.text=(dataList[position].lastMessage as TextMessage).text?:""

        }

//        holder.binding.chatSubTitle.text=(dataList[position].conversationType)
        Glide.with(context).load((dataList[position].conversationWith as User).avatar).into(holder.binding.chatImage)
        holder.itemView.setOnClickListener {
            clickListern.onClick((dataList[position].conversationWith as User).uid)
        }
        holder.binding.chatImage.setOnClickListener {
            clickListern.onClickImg(position)
        }
     /*   holder.binding.chatTimeAgo.text=dataList[position].time
        holder.binding.chatMsgCount.text=dataList[position].msgCount
        holder.binding.chatImage.setImageResource(dataList[position].img)
        if(dataList[position].isVideo){
            holder.binding.chatSubTitle.setText("Video")
            holder.binding.chatSubTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_videocam,0,0,0)
        }else{
            holder.binding.chatSubTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        }
        if(dataList[position].isRead){
           holder.binding.chatTicIcon.setImageResource(R.drawable.ic_ble_double_tic)
        }else{
            holder.binding.chatTicIcon.setImageResource(R.drawable.ic_gray_double_tic)
        }

        holder.itemView.setOnClickListener {
            clickListern.onClick(position)
        }

        holder.binding.chatImage.setOnClickListener {
            clickListern.onClickImg(position)
        }*/
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: String)
        fun onClickImg(pos: Int)
    }
}

