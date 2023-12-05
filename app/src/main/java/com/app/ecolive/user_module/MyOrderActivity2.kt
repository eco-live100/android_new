package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityMyOrder2Binding
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.taximodule.TrackingWithProgressActivity
import com.app.ecolive.user_module.user_adapter.UserMyOrderList2Adapter
import com.app.ecolive.utils.Utils

class MyOrderActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMyOrder2Binding
    lateinit var adapter: UserMyOrderList2Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order2)
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
        adapter = UserMyOrderList2Adapter(this, arrayList,object : UserMyOrderList2Adapter.ClickListener{
            override fun onClick(pos: Int) {
                startActivity(Intent(this@MyOrderActivity2, TrackingWithProgressActivity::class.java))
            }
        })
        binding.recyclerviewMyOrder.adapter = adapter

    }
    private fun statusBarColor() {
        binding.toolbar.toolbarTitle.text="Order History"
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}