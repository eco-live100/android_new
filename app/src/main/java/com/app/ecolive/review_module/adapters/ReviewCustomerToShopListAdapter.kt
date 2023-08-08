package com.app.ecolive.review_module.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.ReviewRiderToCustomerModel


class ReviewCustomerToShopListAdapter(var context: Context, var dataList: ArrayList<ReviewRiderToCustomerModel>) :
    RecyclerView.Adapter<ReviewCustomerToShopListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowLayoutReviewRiderToCustomerBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowLayoutReviewRiderToCustomerBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowLayoutReviewRiderToCustomerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_layout_review_rider_to_customer, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvTime.text=dataList[position].reviewTime
        holder.binding.tvComment.text=dataList[position].comment
        holder.binding.tvName.text=dataList[position].name
        holder.binding.ratingBar.rating=dataList[position].ratingCount
        holder.binding.tvReply.visibility= View.VISIBLE

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

