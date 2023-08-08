package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.ProductDetailColorVariationAdapter
import com.app.ecolive.common_screen.adapters.ProductDetailSimilarProductAdapter
import com.app.ecolive.common_screen.adapters.ProductImageSliderAdapter
import com.app.ecolive.databinding.ActivityProductDetailBinding
import com.app.ecolive.localmodel.PropertyImageListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.payment_module.AddMoneyMainActivity
import com.app.ecolive.review_module.ReviewCustomerToProductActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.ProductModel
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.localmerchants.ui.localModels.DrawerCategoryListModel
import org.json.JSONObject

class ProductDetailActivity : AppCompatActivity() {
    var cartCount =0
    private lateinit var view_pager: ViewPager
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var lytPageIndicator: LinearLayout
    lateinit var adapter: ProductImageSliderAdapter
    lateinit var colorVariationAdapter: ProductDetailColorVariationAdapter
    lateinit var productDetailSimilarProductAdapter: ProductDetailSimilarProductAdapter
    private var currentIndex: Int = 0
    val listModel = ArrayList<PropertyImageListModel>()
    var productId :String =""
    var shopId :String =""
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        if (intent.extras!=null){
            productId=intent.getStringExtra("productId")!!
            vendorShopProductListAPICAll()
        }
        initView()

        binding.icComment.setOnClickListener {
            startActivity(Intent(this@ProductDetailActivity,CommentProductActivity::class.java))
        }

        binding.btnButNow.setOnClickListener {
           /* startActivity(Intent(this@ProductDetailActivity, AddMoneyMainActivity::class.java)
                .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME,true))*/
            startActivity(Intent(this@ProductDetailActivity, MyCartActivity::class.java))
        }
        binding.addtoCartBtn.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                if(cartCount>0){
                    addToCart()
                }

            }
        }
        binding.icWish.setOnClickListener {
           // MyApp.popErrorMsg("","You have to login first",this@ProductDetailActivity)
        }
    }

    private fun goLoginScreen() {
        Utils.showMessage(this@ProductDetailActivity,"You have to login first")
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun initView() {
        view_pager = binding.viewPager
        lytPageIndicator = binding.lytPageIndicator


        productDetailSimilarProductList()
        binding.productDetailVisitShopTxt.setOnClickListener { startActivity(Intent(this@ProductDetailActivity, VisitShopActivity::class.java)) }
        binding.textViewSeeProductReviews.setOnClickListener { startActivity(Intent(this@ProductDetailActivity, ReviewCustomerToProductActivity::class.java)) }

        binding.minus.setOnClickListener {
            if(cartCount>0){
                cartCount--
                binding.quantity.text =cartCount.toString()
            }
        }
        binding.plus.setOnClickListener {

                cartCount++
            binding.quantity.text =cartCount.toString()
        }
    }
    private fun imageSlider(file: List<ProductModel.File>)
    {
        // Utils.changeStatusColor(this, R.color.white)
        //Utils.changeStatusTextColor(this)
        for (element in file){
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



    private fun productDetailSimilarProductList() {
        val similarProductListModel = ArrayList<SimilarProductListModel>()
        var item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch White With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)

        item = SimilarProductListModel("Apple Watch White With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)
        binding.productDetailSimilarProductRecyclerview.layoutManager = GridLayoutManager(this, 2)
        productDetailSimilarProductAdapter = ProductDetailSimilarProductAdapter(this, similarProductListModel)
        binding.productDetailSimilarProductRecyclerview.adapter = productDetailSimilarProductAdapter

    }

    private fun vendorShopProductListAPICAll() {
        progressDialog.show(this)
        var addProductViewModel = CommonViewModel(this)
        var json = JSONObject()
        json.put("productId", productId)
        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.vendorShopProductList(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall:")
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        shopId =it.data.docs[0].storeId
                        homeProductList(it.data)
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
    private fun addToCart() {
        progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)
        var json = JSONObject()
        json.put("shop_id", shopId)
        json.put("qty", cartCount)
        json.put("product_id", productId)
        json.put("purchase_type", "")
        json.put("product_color", "")
        Log.d("ok", "addProductAPICall: " + json)
        addtoCartViewModel.addToCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall:")
                    progressDialog.dialog.dismiss()
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
    private fun homeProductList(data: ProductModel.Data) {
        binding.productName.text =data.docs[0].productData.productName
        binding.productPrice.text =data.docs[0].productData.price
        binding.productLivePrice.text =data.docs[0].productData.priceLive
        binding.freeDelivery.text =data.docs[0].freeDelivery
        binding.fastDelivery.text =data.docs[0].fastDeliver
        binding.aboutDescription.text = data.docs[0].productData.description?:""
        imageSlider(data.docs[0].file)
        val colorVariationList = ArrayList<DrawerCategoryListModel>()


        for (i in 0 until data.docs[0].productData.color.size) {

            try {
                val item = DrawerCategoryListModel( data.docs[0].productData.color[i])
                colorVariationList.add(item)

            } catch (e: Exception) {

            }
        }

        binding.productDetailColorVariationRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        colorVariationAdapter = ProductDetailColorVariationAdapter(this, colorVariationList)
        binding.productDetailColorVariationRecyclerview.adapter = colorVariationAdapter


    }
}