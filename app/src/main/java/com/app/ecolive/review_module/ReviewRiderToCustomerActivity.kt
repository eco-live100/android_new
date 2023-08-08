package com.app.ecolive.review_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityReviewRiderToCustomerBinding
import com.app.ecolive.localmodel.ReviewRiderToCustomerModel
import com.app.ecolive.review_module.adapters.ReviewRiderToCustomerListAdapter
import com.app.ecolive.utils.Utils

class ReviewRiderToCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewRiderToCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_rider_to_customer)
        statusBarColor()
        reviewList()
    }

    private fun reviewList() {


        val dataList = ArrayList<ReviewRiderToCustomerModel>()
        var item = ReviewRiderToCustomerModel("Petey Cruiser","2 week ago",3.5f,"Good Work Environment and Culture Teammates Supportive and The Management is Transparent")
        dataList.add(item)

        item = ReviewRiderToCustomerModel("John doe","2 week ago",4.5f,"Job is good. Good salary good timing less work time... Can earn good income but their is no carrer growth.. experience is not useful anywhere.. this job doesn't have any respect anywhere..it is the only to earn good income with hard work..")
        dataList.add(item)

        item = ReviewRiderToCustomerModel("Greta Life","2 week ago",2.5f,"Here 03 day's before i had a pathetic experience about such a bad negligence by customer care service providers. Because being a delivery i was working sincerely without any remarks but due to their negligence.")
        dataList.add(item)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ReviewRiderToCustomerListAdapter(this, dataList)
        binding.recyclerView.adapter = adapter

    }
    private fun statusBarColor() {
        binding.include2.toolbarTitle.text="Reviews"
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}