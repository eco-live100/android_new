package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.GetStartedActivity
import com.app.ecolive.common_screen.adapters.HomeProductListAdapter
import com.app.ecolive.common_screen.adapters.ProductDetailSimilarProductAdapter
import com.app.ecolive.common_screen.adapters.ProductImageSliderAdapter
import com.app.ecolive.common_screen.adapters.ProductListAdapter
import com.app.ecolive.databinding.ActivityProductDetailBinding
import com.app.ecolive.databinding.ActivityProductListBinding
import com.app.ecolive.localmodel.HomeProductListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.RestaurantProfileActivity
import com.app.ecolive.shop_owner.model.ProductModel
import com.app.ecolive.user_module.user_adapter.FoodListAdapter
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.bumptech.glide.Glide
import com.offercity.base.BaseActivity
import org.json.JSONObject

class ProductListActivity : BaseActivity() {
    private lateinit var binding: ActivityProductListBinding
    lateinit var adapter: ProductListAdapter
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        initView()
        productList()
        statusBarColor()
        if (intent.getStringExtra(AppConstant.CATEGORY) == AppConstant.GROCERY) {
            binding.foodTopLogo.setImageResource(R.drawable.shop_detail_topimg4x)
            binding.textTop1.text = "Fresh Groceries"
            binding.textTop1.setTextColor(resources.getColor(R.color.black))
            binding.textTop2.setTextColor(resources.getColor(R.color.black))
            binding.toolbar.toolbarTitle.setText("Groceries")
        } else if (intent.getStringExtra(AppConstant.CATEGORY) == AppConstant.PHARMACY) {
            binding.foodTopLogo.setImageResource(R.drawable.ic_pharmacylogo)
            binding.textTop1.text = "Eco-Pharmacy"
            binding.textTop2.text = "Fastest and safest way to get your medication"
            // binding.textTop1.setTextColor(resources.getColor(R.color.color_4FB84A))
            binding.textTop1.setTextColor(resources.getColor(R.color.color_548235))
            binding.textTop2.setTextColor(resources.getColor(R.color.black))
            binding.toolbar.toolbarTitle.setText("Pharmacy")
        } else if (intent.getStringExtra(AppConstant.CATEGORY) == AppConstant.RETAIL) {
            Glide
                .with(this@ProductListActivity)
                .load(R.drawable.ic_retaillogo)
                .placeholder(R.drawable.appicon_512)
                .into(binding.foodTopLogo);
            //    binding.foodTopLogo.setImageResource(R.drawable.ic_retaillogo)
            binding.textTop2.text = "Shop live: all deliverable in \n" + "Maximum 1 hour!"
            binding.textTop1.text = ""

            binding.textTop1.setTextColor(resources.getColor(R.color.black))
            binding.textTop2.setTextColor(resources.getColor(R.color.black))
            binding.toolbar.toolbarTitle.text = "Retail"
        } else {
            binding.toolbar.toolbarTitle.text = "Food"
        }
    }

    private fun initView() {
        binding.toolbar.ivBack.setOnClickListener { finish() }


    }

    private fun productList() {
        val similarProductListModel = ArrayList<SimilarProductListModel>()
        var item = SimilarProductListModel(
            "Apple Watch Gold With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch Gold With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch Gold With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch),"",""
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white),"",""
        )
        similarProductListModel.add(item)

        if (intent.getStringExtra(AppConstant.CATEGORY) == AppConstant.FOOD) {
            binding.productListRecyclerview.layoutManager =
                LinearLayoutManager(this@ProductListActivity, LinearLayoutManager.VERTICAL, false)
            var foodListAdapter = FoodListAdapter(
                this@ProductListActivity,
                similarProductListModel,
                false,
                object : FoodListAdapter.ClickListener {
                    override fun onClick(pos: Int) {
                        startActivity(
                            Intent(
                                this@ProductListActivity,
                                RestaurantProfileActivity::class.java
                            )
                        )
                    }

                })
            binding.productListRecyclerview.adapter = foodListAdapter
        }
        else if (intent.getStringExtra(AppConstant.CATEGORY) == AppConstant.GROCERY) {
            binding.productListRecyclerview.layoutManager =
                LinearLayoutManager(this@ProductListActivity, LinearLayoutManager.VERTICAL, false)
            var foodListAdapter = FoodListAdapter(
                this@ProductListActivity,
                similarProductListModel,
                true,
                object : FoodListAdapter.ClickListener {
                    override fun onClick(pos: Int) {
                        startActivity(Intent(this@ProductListActivity, VisitShopActivity::class.java).putExtra("from","grocery"))
                    }

                })
            binding.productListRecyclerview.adapter = foodListAdapter
        }
        else if (intent.getStringExtra(AppConstant.CATEGORY) == AppConstant.PHARMACY) {
            binding.productListRecyclerview.layoutManager =
                StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = ProductListAdapter(
                this,
                similarProductListModel,
                true,
                object : ProductListAdapter.ClickListener {
                    override fun onClick(pos: Int) {
                        startActivity(
                            Intent(
                                this@ProductListActivity,
                                ProductDetailActivity::class.java
                            )
                        )
                    }
                })
            binding.productListRecyclerview.adapter = adapter
        } else {
            vendorShopProductListAPICAll()
        }


    }

    private fun statusBarColor() {
        //  Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor2(this)
    }

    private fun vendorShopProductListAPICAll() {
         progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()
        json.put("categoryId", "6351876d9c5b36484345bda6")
        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.vendorShopProductList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                      progressDialog.dialog.dismiss()
                    it.data?.let {
                        homeProductList(it.data)
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

    private fun homeProductList(data: ProductModel.Data) {


        val similarProductListModel = ArrayList<SimilarProductListModel>()
        for (i in 0 until data.docs.size) {

            try {
                var item = SimilarProductListModel(

                    data.docs[i].productData.productName,
                    data.docs[i].productData.price,
                    data.docs[i].productData.priceLive,
                    resources.getDrawable(R.drawable.product_image3),
                    data.docs[i].file[0].name,
                    data.docs[i].productData.subCatogary,
                )
                similarProductListModel.add(item)

            } catch (e: Exception) {


            }
        }

        binding.productListRecyclerview.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        adapter = ProductListAdapter(
            this,
            similarProductListModel,
            false,
            object : ProductListAdapter.ClickListener {
                override fun onClick(pos: Int) {
                    startActivity(
                        Intent(
                            this@ProductListActivity,
                            ProductDetailActivity::class.java
                        )
                    )
                }
            })
        binding.productListRecyclerview.adapter = adapter


    }
}