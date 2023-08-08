package com.app.ecolive.rider_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.ProductListAdapter
import com.app.ecolive.databinding.ActivityCurrentOrderListBinding
import com.app.ecolive.databinding.ActivityProductListBinding
import com.app.ecolive.localmodel.CurrentOrderListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.rider_module.adapter.CurrentOrderListAdapter
import com.app.ecolive.user_module.ProductDetailActivity
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.Utils.Companion.changeStatusColor
import com.app.ecolive.utils.Utils.Companion.changeStatusTextColor

class CurrentOrderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentOrderListBinding
    lateinit var adapter: CurrentOrderListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_current_order_list)
        initView()
        productList()
        statusBarColor()
    }

    private fun initView() {
        binding.toolbar.toolbarTitle.text="Current Order"
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    private fun productList() {


        val currentListModel = ArrayList<CurrentOrderListModel>()
        var item = CurrentOrderListModel("Angie O. Plasty","20 Nov 2020","03:20 PM","15 MIN","$1200","Debit card",resources.getDrawable(R.drawable.dummy_male_user))
        currentListModel.add(item)
        item = CurrentOrderListModel("Sharon Needles","20 Nov 2020","03:20 PM","15 MIN","$1200","Debit card",resources.getDrawable(R.drawable.dummy_female_user))
        currentListModel.add(item)
        item = CurrentOrderListModel("Angie O. Plasty","20 Nov 2020","03:20 PM","15 MIN","$1200","Debit card",resources.getDrawable(R.drawable.dummy_male_user))
        currentListModel.add(item)
        item = CurrentOrderListModel("Sharon Needles","20 Nov 2020","03:20 PM","15 MIN","$1200","Debit card",resources.getDrawable(R.drawable.dummy_female_user))
        currentListModel.add(item)
        item = CurrentOrderListModel("Angie O. Plasty","20 Nov 2020","03:20 PM","15 MIN","$1200","Debit card",resources.getDrawable(R.drawable.dummy_male_user))
        currentListModel.add(item)
        binding.orderListRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = CurrentOrderListAdapter(this, currentListModel,object : CurrentOrderListAdapter.ClickListener{
            override fun onAccept(pos: Int) {
                startActivity(Intent(this@CurrentOrderListActivity, AcceptedDeclineOrderActivity::class.java))
            }

            override fun onDecline(pos: Int) {
                startActivity(Intent(this@CurrentOrderListActivity, AcceptedDeclineOrderActivity::class.java))
            }
        })
        binding.orderListRecyclerview.adapter = adapter

    }
    private fun statusBarColor() {
       changeStatusColor(this, R.color.color_050D4C)
       changeStatusTextColor(this)
    }
}