package com.app.ecolive.payment_module.adapters
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.payment_module.model.TransactionHistoryModel
import com.app.ecolive.utils.Utils


class TransactionHistoryListAdapter(
    var context: Context,
    var dataList: ArrayList<TransactionHistoryModel.Doc>,  ) :
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.binding.tvUserName.text=dataList[position].receiverDetails.firstName?:""
        }catch (e:Exception){
            holder.binding.tvUserName.text=""
        }
        holder.binding.tvPaymentDateTime.text=Utils.formatDate(dataList[position].createdAt)
        holder.binding.tvAmount.text=dataList[position].amount
        //holder.binding.ivUserProfileImage.setImageDrawable(dataList[position].profileImage)

        /*if (dataList[position].status=="minus")
        {
            holder.binding.tvAmount.setTextColor(context.resources.getColor(R.color.color_red))
        }else
        {
            holder.binding.tvAmount.setTextColor(context.resources.getColor(R.color.color_blue))

        }*/
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

