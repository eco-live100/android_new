package com.app.ecolive.user_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.ProductListAdapter
import com.app.ecolive.databinding.ActivityProductListBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity

class ProductListActivity : BaseActivity() {
    private lateinit var binding: ActivityProductListBinding
    lateinit var adapter: ProductListAdapter
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        initView()

        statusBarColor()
        if (intent.getStringExtra(AppConstant.CATEGORY) == "Live") {
            vendorShopProductListAPICAll()
        }else{
            vendorShopProductListAPICAll2()
        }
    }

    private fun initView() {
        binding.toolbar.ivBack.setOnClickListener { finish() }

        binding.productListRecyclerview.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        adapter = ProductListAdapter(
            this,
            arrayListOf(), arrayListOf(),
            true,
            object : ProductListAdapter.ClickListener {
                override fun onClick(productId: String) {
                    startActivity(
                        Intent(
                            this@ProductListActivity,
                            ProductDetailActivity::class.java
                        ).putExtra("productId", productId)
                    )
                }
            })
        binding.productListRecyclerview.adapter = adapter
        binding.edtSearch.doAfterTextChanged { adapter?.filter?.filter(it) }
    }


    private fun statusBarColor() {
          Utils.changeStatusColor(this, R.color.color_050D4C)
     }

    private fun vendorShopProductListAPICAll() {
         progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = HashMap<String,String>()
        json.put("distance","3")
        json.put("latitude",MyApp.locationLast!!.latitude.toString())
        json.put("longitude",MyApp.locationLast!!.longitude.toString())

        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.vendorShopProductList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                      progressDialog.dialog.dismiss()
                    it.data?.let {
                        adapter.update(it.data.docs)
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                      progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun vendorShopProductListAPICAll2() {
         progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = HashMap<String,String>()



        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.vendorShopProductList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                      progressDialog.dialog.dismiss()
                    it.data?.let {
                       adapter.update(it.data.docs)
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                      progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
}