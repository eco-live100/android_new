package com.app.ecolive.rider_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAceptedDeclineOrderBinding
import com.app.ecolive.databinding.ActivityCurrentOrderListBinding
import com.app.ecolive.localmodel.AcceptDeclineOrderListModel
import com.app.ecolive.localmodel.CurrentOrderListModel
import com.app.ecolive.rider_module.adapter.AcceptDeclineOrderListAdapter
import com.app.ecolive.rider_module.adapter.CurrentOrderListAdapter
import com.app.ecolive.utils.Utils

class AcceptedDeclineOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAceptedDeclineOrderBinding
    lateinit var adapter: AcceptDeclineOrderListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_acepted_decline_order)
        initView()
        productList()
        statusBarColor()
    }
    private fun initView() {
        binding.toolbar.toolbarTitle.text="My orders"
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    private fun productList() {


        val currentListModel = ArrayList<AcceptDeclineOrderListModel>()
        var item = AcceptDeclineOrderListModel("0345","20 Nov 2020","03:20 PM","John doe's 10 TH orders","2 x Smart watch","1 x Men shoes","$410","$210","Total bill $620","Accepted")
        currentListModel.add(item)
        item = AcceptDeclineOrderListModel("0345","20 Nov 2020","03:20 PM","John doe's 10 TH orders","2 x Men shoes","1 x Men shoes","$410","$210","Total bill $620","Decline")
        currentListModel.add(item)
        item = AcceptDeclineOrderListModel("0345","20 Nov 2020","03:20 PM","John doe's 10 TH orders","2 x Smart watch","1 x Headphone","$510","$210","Total bill $720","Accepted")
        currentListModel.add(item)
        item = AcceptDeclineOrderListModel("0345","20 Nov 2020","03:20 PM","John doe's 10 TH orders","2 x Men shoes","1 x Men shoes","$610","$210","Total bill $820","Accepted")
        currentListModel.add(item)
        item = AcceptDeclineOrderListModel("0345","20 Nov 2020","03:20 PM","John doe's 10 TH orders","2 x Smart watch","1 x Headphone","$410","$210","Total bill $620","Decline")
        currentListModel.add(item)

        binding.orderListRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AcceptDeclineOrderListAdapter(this, currentListModel,object : AcceptDeclineOrderListAdapter.ClickListener{
            override fun onClick(pos: Int) {
                //startActivity(Intent(this@CurrentOrderListActivity, Cur::class.java))
            }
        })
        binding.orderListRecyclerview.adapter = adapter

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}