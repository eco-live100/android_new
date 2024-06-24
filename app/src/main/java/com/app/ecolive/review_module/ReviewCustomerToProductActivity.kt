package com.app.ecolive.review_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.review_module.adapters.ReviewCustomerToProductListAdapter
import com.app.ecolive.databinding.ActivityReviewCustomerToProductBinding
import com.app.ecolive.localmodel.CurrentOrderListModel
import com.app.ecolive.utils.Utils

class ReviewCustomerToProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewCustomerToProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_customer_to_product)
        statusBarColor()
        productReviewList()
    }
    private fun statusBarColor() {
        binding.toolbar.toolbarTitle.text="Reviews"
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun productReviewList() {




    }

}