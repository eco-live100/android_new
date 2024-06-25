package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.ProductDetailColorVariationAdapter
import com.app.ecolive.common_screen.adapters.ProductDetailSimilarProductAdapter
import com.app.ecolive.common_screen.adapters.ProductImageSliderAdapter
import com.app.ecolive.databinding.ActivityProductDetailBinding
import com.app.ecolive.localmodel.PropertyImageListModel
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.ProductDetailModel
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class ProductDetailActivity : AppCompatActivity() {
    private var productPrice: Double = 0.0
    var cartCount = 0
    var OldcartCount = 0
    private lateinit var view_pager: ViewPager
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var lytPageIndicator: LinearLayout
    lateinit var adapter: ProductImageSliderAdapter

    private var currentIndex: Int = 0
    val listModel = ArrayList<PropertyImageListModel>()
    var productId: String = ""
    var shopId: String = ""
    private val progressDialog = CustomProgressDialog()
    var alreadyInCartProductId = ""
    var alreadyInCartId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        if (intent.extras != null) {
            productId = intent.getStringExtra("productId")!!
            productDetailApi()
        }
        initView()


        binding.myCart.setOnClickListener {
            startActivity(Intent(this@ProductDetailActivity, MyCartActivity::class.java))

        }

        binding.btnButNow.setOnClickListener {

            startActivity(Intent(this@ProductDetailActivity, MyCartActivity::class.java))
        }
        binding.addtoCartBtn.setOnClickListener {

            if (cartCount > 0) {
                addToCart()
            }

        }

    }

    override fun onStart() {
        super.onStart()
        getCart()
    }


    private fun initView() {
        Utils.changeStatusColor(this, R.color.color_050D4C)

        view_pager = binding.viewPager
        lytPageIndicator = binding.lytPageIndicator

        binding.minus.setOnClickListener {
            if (cartCount > 0) {
                cartCount--
                binding.quantity.text = cartCount.toString()
            }
        }
        binding.plus.setOnClickListener {

            cartCount++
            binding.quantity.text = cartCount.toString()
        }
    }

    private fun imageSlider(file: ArrayList<ProductDetailModel.Image>) {

        for (element in file) {
            val item = PropertyImageListModel(element.name)
            listModel.add(item)
        }

        view_pager = binding.viewPager
        lytPageIndicator = binding.lytPageIndicator
        adapter = ProductImageSliderAdapter(applicationContext, listModel)
        view_pager.adapter = adapter
        addPageIndicators()
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                updatePageIndicator(position)
            }
        })
    }

    private fun addPageIndicators() {
        lytPageIndicator.removeAllViews()
        for (i in listModel.indices) {
            val view = ImageView(applicationContext)
            view.setImageResource(R.drawable.ic_inactive_dot)

            lytPageIndicator.addView(view)
        }
        updatePageIndicator(currentIndex)
    }

    private fun updatePageIndicator(position: Int) {
        var imageView: ImageView

        val lp =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        lp.setMargins(16, 0, 16, 0)
        for (i in 0 until lytPageIndicator.childCount) {
            imageView = lytPageIndicator.getChildAt(i) as ImageView
            imageView.layoutParams = lp
            when (position) {
                i -> {
                    imageView.setImageResource(R.drawable.ic_active_dot)
                }

                else -> {
                    imageView.setImageResource(R.drawable.ic_inactive_dot)
                }
            }
        }
    }

    private fun getCart() {
        //  progressDialog.show(this)
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

                    //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        //  productList(it.data.products)
                        alreadyInCartProductId = it.data.products[0].productId._id ?: ""
                        if (alreadyInCartProductId == productId) {
                            cartCount =it.data.totalQty
                            OldcartCount =it.data.totalQty

                            binding.quantity.text =cartCount.toString()
                        }
                        alreadyInCartId = it.data._id?: ""
                        binding.count.text =it.data.totalQty.toString()


                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    // progressDialog.dialog.dismiss()
                    /* var vv = it.message
                     // var msg = JSONObject(it.message)
                     // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                     MyApp.popErrorMsg("", "" + vv, this)*/
                }
            }
        }
    }

    private fun removeCart() {
        //  progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)
        var json = JSONObject()
        json.put("cartId", alreadyInCartId)
        /* json.put("shop_id", shopId)
         json.put("qty", cartCount)
         json.put("product_id", productId)
         json.put("purchase_type", "")
         json.put("product_color", "")*/
        Log.d("ok", "getCart: " + json)
        addtoCartViewModel.removeCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        //  productList(it.data.products)
                       alreadyInCartProductId =""
                        alreadyInCartId=""
                        addToCart()

                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    // progressDialog.dialog.dismiss()
                    /* var vv = it.message
                     // var msg = JSONObject(it.message)
                     // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                     MyApp.popErrorMsg("", "" + vv, this)*/
                }
            }
        }
    }

    private fun productDetailApi() {
        binding.consButton.visibility = View.GONE
        binding.nestedScrollView.visibility = View.GONE
        binding.layoutShimmer.visibility = View.VISIBLE
        // progressDialog.show(this)
        var addProductViewModel = CommonViewModel(this)
        var json = JSONObject()
        json.put("productId", productId)
        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.productDetailApi(productId).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    binding.consButton.visibility = View.VISIBLE
                    binding.nestedScrollView.visibility = View.VISIBLE
                    binding.layoutShimmer.visibility = View.GONE
                    Log.d("ok", "productListAPICall:")
                    // progressDialog.dialog.dismiss()
                    it.data?.let {
                        //  shopId =it.data.docs[0].storeId
                        homeProductList(it.data)
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    // progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }

    private fun addToCart() {
        if (alreadyInCartProductId !="")
        if (alreadyInCartProductId != productId) {
            val builder = MaterialAlertDialogBuilder(this, R.style.Theme_MyApp_Dialog_Alert)
            // val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Item different").setMessage("Added product in cart different. You want to replace it")
                .setPositiveButton("OK") { dialog, which -> removeCart() }

            builder.setNegativeButton("Cancel"){dialog, which -> dialog.dismiss()}
            val alert = builder.create()
            alert.show()
            return
        }
        progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)

        var json = JSONObject()
        json.put("shopId", shopId)
        if (OldcartCount < cartCount){
            var count =cartCount -OldcartCount
            json.put("qty", count)
        }else if (OldcartCount >cartCount){
            var count =OldcartCount -cartCount
            json.put("qty", -count)
        }else{
            var count =0
            json.put("qty", count)
        }

        json.put("productId", productId)
        json.put("price", productPrice)

        Log.d("ok", "addProductAPICall: " + json)
        addtoCartViewModel.addToCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall:")
                    progressDialog.dialog.dismiss()
                    getCart()
                    it.data?.let {

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

    private fun homeProductList(data: ProductDetailModel.Data) {
        if (data.outofstock) {


            binding.textView33.visibility = View.INVISIBLE
            binding.constraintLayout6.visibility = View.INVISIBLE
        } else {
            binding.textView33.visibility = View.VISIBLE
            binding.constraintLayout6.visibility = View.VISIBLE
        }
        shopId = data.vendorShopId
        productPrice = data.price.toString().toDouble()
        binding.productName.text = data.name
        binding.productPrice.text = data.livePrice.toString()
        binding.productLivePrice.text = data.price.toString()
        binding.freeDelivery.text = data.freeDelivery
        binding.fastDelivery.text = data.fastDeliver
        binding.aboutDescription.text = data.productData ?: ""
        binding.stockStatus.text = if (data.outofstock) {
            "OUT OF STOCK"
        } else {
            "IN STOCK"
        }
        imageSlider(data.images)


    }
}