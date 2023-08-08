package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.MyCartListAdapter
import com.app.ecolive.databinding.ActivityMyCartBinding
import com.app.ecolive.localmodel.MyCartListModel
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.GetCartModel
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import org.json.JSONObject

class MyCartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyCartBinding
    lateinit var adapter: MyCartListAdapter
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_cart)
        initView()

        statusBarColor()
        getCart()
        binding.appCompatBuyNow.setOnClickListener {
            startActivity(Intent(this@MyCartActivity,UserPaymentCheckoutActivity::class.java))
        }
    }
    private fun initView() {
        binding.toolbar.toolbarTitle.text="Cart"
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    private fun productList(data: List<GetCartModel.Data>) {

        val currentListModel = ArrayList<MyCartListModel>()

        for(i in 0 until  data.size){
            var item = MyCartListModel(data[i].qty,data[i].products.productData.productName.toString(),data[i].products.productData.priceLive.toString(),data[i].products.imageUrl[0].name.toString())
            currentListModel.add(item)
        }

        binding.orderListRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = MyCartListAdapter(this, currentListModel,object : MyCartListAdapter.ClickListener{
            override fun onClick(pos: Int) {
                //startActivity(Intent(this@CurrentOrderListActivity, Cur::class.java))
            }

            override fun onDelete(pos: Int) {

            }

            override fun onMinus(pos: Int) {

            }

            override fun onPlus(pos: Int) {

            }
        })
        binding.orderListRecyclerview.adapter = adapter

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun getCart() {
        progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)
        var json = JSONObject()
       /* json.put("shop_id", shopId)
        json.put("qty", cartCount)
        json.put("product_id", productId)
        json.put("purchase_type", "")
        json.put("product_color", "")*/
        Log.d("ok", "getCart: " + json)
        addtoCartViewModel.getCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        productList(it.data)
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

    private fun deleteCartItem() {
        progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)
        var json = JSONObject()
        /* json.put("shop_id", shopId)
         json.put("qty", cartCount)
         json.put("product_id", productId)
         json.put("purchase_type", "")
         json.put("product_color", "")*/
        Log.d("ok", "getCart: " + json)
        addtoCartViewModel.getCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        productList(it.data)
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