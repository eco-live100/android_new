package com.app.ecolive.shop_owner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ProductlistActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity

class ProductListActivity : BaseActivity() {
    lateinit var binding: ProductlistActivityBinding
   lateinit var storeData : ShopListModel.Data
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ProductListActivity,R.layout.productlist_activity)
        setToolBar()
        initView()
        getDataIntent()
        vendorShopProductListAPICAll()
    }

    private fun vendorShopProductListAPICAll() {
        progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = HashMap<String,String>()
        json.put("shopId", storeData._id)
        json.put("page", "1")
        json.put("limit", "100")
        Log.d("ok", "addProductAPICall: "+json)
        addProductViewModel.shopProductListByid(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                    /*  binding.productListRecyclerview.apply {
                          layoutManager =LinearLayoutManager(this@ProductListActivity)
                          adapter =ShopProductListAdapter(this@ProductListActivity,it.data.)
                      }*/
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" +vv, THIS!!)
                }
            }
        }
    }

    private fun initView() {
        setTouchNClick(binding.productListPluse)
    }

    private fun getDataIntent() {
        storeData = intent.getSerializableExtra(AppConstant.STORE_DATA) as ShopListModel.Data


    }

    private fun setToolBar() {
        Utils.changeStatusTextColor2(this)
        binding.toolbarProductList.toolbarTitle.text = "Product"
        binding.toolbarProductList.ivBack.setOnClickListener { finish() }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if(v==binding.productListPluse){
            if(storeData!=null) {
                startActivity(Intent(this@ProductListActivity, AddProductActvity::class.java)
                    .putExtra(AppConstant.STORE_DATA,storeData))
            }

        }
    }
}