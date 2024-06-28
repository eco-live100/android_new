package com.app.ecolive.user_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityMyOrderBinding
import com.app.ecolive.service.Status
import com.app.ecolive.user_module.user_adapter.UserMyOrderListAdapter
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel

class MyOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyOrderBinding
    lateinit var adapter: UserMyOrderListAdapter
    private val progressDialog = CustomProgressDialog()
    var shopId =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_order)
        shopId =intent.getStringExtra("ID")?:""
        statusBarColor()
        initView()
        orderList()
    }
    private fun initView() {
        binding.toolbar.ivBack.setOnClickListener { finish() }
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
        addProductViewModel.orderListShop(shopId,json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                    progressDialog.dialog.dismiss()
                    binding.recyclerviewMyOrder.layoutManager = LinearLayoutManager(this)
                    adapter = UserMyOrderListAdapter(this, it.data!!.data.items,object : UserMyOrderListAdapter.ClickListener{
                        override fun onClick(data: String, id: String) {
                            updateOrderStatus(status = data,id)
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


    private fun updateOrderStatus(status: String, id: String) {
        progressDialog.show(this)
        var addProductViewModel = CommonViewModel(this)
        var json = HashMap<String, String>()
        // json.put("shopId", storeData._id)
        json.put("status", status)

        addProductViewModel.updateOrderStatus(id,json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                    progressDialog.dialog.dismiss()
                   for (i in 0 until adapter.dataList.size){
                       if (adapter.dataList[i].orderNumber ==it.data?.order?.orderNumber){
                           adapter.dataList[i].status = it.data.order.status
                           adapter.notifyDataSetChanged()
                           break
                       }
                   }
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