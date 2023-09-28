package com.app.ecolive.payment_module.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.payment_module.model.Data


class UserListForAdapter(var list: ArrayList<Data>, var clickListern:ClickListener) :
    RecyclerView.Adapter<UserListForAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowUserListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowUserListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowUserListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_user_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userNameTxt.text =
            "${list[position].firstName ?: ""} ${list[position].lastName ?: ""}"
        holder.binding.userPhoneTxt.text =list[position].mobileNumber
        if (!list[position].firstName.isNullOrEmpty()){
             holder.binding.nameLogo.text =list[position].firstName.substring(0,1)
        }

        holder.binding.mainView.setOnClickListener {
            clickListern.onClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ClickListener {
        fun onClick(pos: Data)

    }
}

