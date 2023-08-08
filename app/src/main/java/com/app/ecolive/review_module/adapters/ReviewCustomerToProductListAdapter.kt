package com.app.ecolive.review_module.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.localmodel.CurrentOrderListModel


class ReviewCustomerToProductListAdapter(var context: Context, var dataList: ArrayList<CurrentOrderListModel>) :
    RecyclerView.Adapter<ReviewCustomerToProductListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowLayoutReviewCustomerToProductBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowLayoutReviewCustomerToProductBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowLayoutReviewCustomerToProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_layout_review_customer_to_product, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }

    interface ClickListener {
        fun onClick(pos: Int)
    }
}

