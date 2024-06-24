package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityMyOrder2Binding
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.TrackingWithProgressActivity
import com.app.ecolive.user_module.user_adapter.UserMyOrderList2Adapter
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel

class MyOrderActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMyOrder2Binding
    lateinit var adapter: UserMyOrderList2Adapter
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order2)
        statusBarColor()
        initView()
        orderList()
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


    }
    private fun statusBarColor() {
        binding.toolbar.toolbarTitle.text="Order History"
        Utils.changeStatusColor(this, R.color.color_050D4C)

    }

    private fun orderList() {
        progressDialog.show(this)
        var addProductViewModel = CommonViewModel(this)
        var json = HashMap<String, String>()
       // json.put("shopId", storeData._id)
        json.put("status", "")
        json.put("paymentMethod", "")
        json.put("paymentStatus", "")
        json.put("page", "1")
        json.put("limit", "100")
        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.orderList(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                    progressDialog.dialog.dismiss()
                    binding.recyclerviewMyOrder.layoutManager = LinearLayoutManager(this)
                    adapter = UserMyOrderList2Adapter(this, it.data!!.data.items,object : UserMyOrderList2Adapter.ClickListener{
                        override fun onClick(pos: Int) {
                            startActivity(Intent(this@MyOrderActivity2, TrackingWithProgressActivity::class.java))
                        }
                    })
                    binding.recyclerviewMyOrder.adapter = adapter
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }

}