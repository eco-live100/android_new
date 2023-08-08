package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityMyOrderBinding
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.rider_module.TrackingWithProgressActivity
import com.app.ecolive.user_module.user_adapter.UserMyOrderListAdapter
import com.app.ecolive.utils.Utils

class MyOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyOrderBinding
    lateinit var adapter: UserMyOrderListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order)
        statusBarColor()
        initView()
        productList()
    }
    private fun initView() {
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    private fun productList() {
        val arrayList = ArrayList<MyOrderListModel>()
        var item = MyOrderListModel("(4.1)","Relish analogue men's watch","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.product_image1))
        arrayList.add(item)
        item = MyOrderListModel("(3.8)","The best Beats headphones","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.apple_watch_white))
        arrayList.add(item)
        item = MyOrderListModel("(4.1)","Black office chair","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.apple_watch))
        arrayList.add(item)
        item = MyOrderListModel("(4.1)","Lunch box","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.apple_watch_white))
        arrayList.add(item)
        item = MyOrderListModel("(4.1)","Relish analogue men's watch","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.product_image3))
        arrayList.add(item)
        binding.recyclerviewMyOrder.layoutManager = LinearLayoutManager(this)
        adapter = UserMyOrderListAdapter(this, arrayList,object : UserMyOrderListAdapter.ClickListener{
            override fun onClick(pos: Int) {
                startActivity(Intent(this@MyOrderActivity, TrackingWithProgressActivity::class.java))
            }
        })
        binding.recyclerviewMyOrder.adapter = adapter

    }
    private fun statusBarColor() {
        binding.toolbar.toolbarTitle.text="My Order"
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}