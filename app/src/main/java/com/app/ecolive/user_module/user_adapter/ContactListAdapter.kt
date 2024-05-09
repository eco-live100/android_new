package com.app.ecolive.user_module.user_adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.payment_module.model.Data
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton


class ContactListAdapter(var list: ArrayList<Data>, var clickListern:  ClickListener) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowContactlistBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowContactlistBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding: RowContactlistBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_contactlist, parent, false)
            return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        "${list[position].firstName ?: ""} ${list[position].lastName ?: ""}".also { holder.binding.tvUserName.text = it }
        holder.binding.tvSubTitle.text=list[position].mobileNumber
        holder.binding.ivUserImage.setImageResource(R.drawable.ic_user_blue)



    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ClickListener {
        fun onClick(pos: Data, type: String, ivPhone: ZegoSendCallInvitationButton)

    }
}

