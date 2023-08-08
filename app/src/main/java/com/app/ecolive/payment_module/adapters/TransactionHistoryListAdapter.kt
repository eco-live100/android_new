package com.app.ecolive.payment_module.adapters
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
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.localmodel.TransactionHistoryListModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel


class TransactionHistoryListAdapter(var context: Context, var dataList: ArrayList<TransactionHistoryListModel>) :
    RecyclerView.Adapter<TransactionHistoryListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowTransactionHistoryBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowTransactionHistoryBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowTransactionHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_transaction_history, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvUserName.text=dataList[position].userName
        holder.binding.tvPaymentDateTime.text=dataList[position].transactionDateTime
        holder.binding.tvAmount.text=dataList[position].amount
        holder.binding.ivUserProfileImage.setImageDrawable(dataList[position].profileImage)

        if (dataList[position].status=="minus")
        {
            holder.binding.tvAmount.setTextColor(context.resources.getColor(R.color.color_red))
        }else
        {
            holder.binding.tvAmount.setTextColor(context.resources.getColor(R.color.color_blue))

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

