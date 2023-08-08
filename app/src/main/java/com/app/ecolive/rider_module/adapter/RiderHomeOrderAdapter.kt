package com.app.ecolive.rider_module.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.rider_module.model.RiderHomeOrderModel


class RiderHomeOrderAdapter(var context: Context, var dataList: ArrayList<RiderHomeOrderModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<RiderHomeOrderAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowVndrCrntorderBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowVndrCrntorderBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowVndrCrntorderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_vndr_crntorder, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.CurrentOrderUserName.text=dataList[position].currentOrderUserName
//
         holder.itemView.setOnClickListener { onClickListener.onClik(position) }
//        holder.binding.btnDecline.setOnClickListener { onClickListener.onDecline(position) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClik(pos: Int)

    }
}

