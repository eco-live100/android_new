package com.app.ecolive.user_module.user_adapter
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
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.user_module.model.UserModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class ContactListAdapter(var context: Context, var dataList: ArrayList<UserModel>, var onClickListener: ClickListener) :
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

        holder.binding.tvUserName.text=dataList[position].title
        holder.binding.tvSubTitle.text=dataList[position].subTitle
        holder.binding.ivUserImage.setImageResource(dataList[position].img)
        holder.binding.tvSubTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(dataList[position].img2,0,0,0)

        holder.binding.ivPhone.setOnClickListener {
            onClickListener.onClick(position)
        }
        //holder.binding.constraintNext.setOnClickListener { onClickListener.onClick(position) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

