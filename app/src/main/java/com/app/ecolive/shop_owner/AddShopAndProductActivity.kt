package com.app.ecolive.shop_owner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.ProductDetailColorVariationAdapter
import com.app.ecolive.databinding.ActivityAddShopAndProductBinding
import com.app.ecolive.localmodel.AddProductImageListModel
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.shop_owner.adapters.AddProductColorVariationAdapter
import com.app.ecolive.shop_owner.adapters.AddProductImageAdapter
import com.app.ecolive.shop_owner.adapters.ShopOwnerProductListAdapter
import com.app.ecolive.shop_owner.adapters.VendorProductListAdapter
import com.app.ecolive.utils.Utils
import com.localmerchants.ui.localModels.DrawerCategoryListModel
import devmike.jade.com.listeners.OnClickStepListener

class AddShopAndProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddShopAndProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop_and_product)
        statusBarColor()

        binding.toolbar.ivBack.setOnClickListener {finish()}

        addShopInfoStepOne()

        binding.includeStepOne.btnAccept.setOnClickListener {
         addShopInfoStepTwo()
        }
        binding.includeStepTwo.btnAccept.setOnClickListener {
        addShopInfoStepThree()
        }

        binding.includeStepThree.btnUpload.setOnClickListener {
           productListWithAddBtn()
        }

        binding.includeProductList.btnAddProduct.setOnClickListener {
           addProductStepOne()
        }

        binding.includeProductStepOne.btnUpload.setOnClickListener {
            addProductStepTwo()
        }
        binding.includeProductStepOne.btnDecline.setOnClickListener {
            productListWithAddBtn()
        }

        binding.includeProductStepTwo.btnNext.setOnClickListener {
           addProductStepThree()
        }

        binding.includeProductStepThree.btnAccept.setOnClickListener { finish() }

        binding.pageStepper.setOnClickStepListener(object : OnClickStepListener {
            override fun onClickStep(position: Int) {
                when (position) {
                    0 -> {
                       addShopInfoStepOne()
                    }
                    1 -> {
                        addShopInfoStepTwo()
                    }
                    2 -> {
                        addShopInfoStepThree()
                    }
                }
            }
        })


        binding.pageStepperProduct.setOnClickStepListener(object : OnClickStepListener {
            override fun onClickStep(position: Int) {
                when (position) {
                    0 -> {
                        addProductStepOne()
                    }
                    1 -> {
                        addProductStepTwo()
                    }
                    2 -> {
                        addProductStepThree()
                    }
                }
            }
        })

        binding.textViewTabShopInfo.setOnClickListener {
            binding.constraintShopInfo.visibility=View.VISIBLE
            binding.constraintProduct.visibility=View.GONE
            binding.textViewTabShopInfo.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_round_gray_5dp))
            binding.textViewTabProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_round_white_round_5dp))
            binding.pageStepper.setCurrentStepPosition(0)
            addShopInfoStepOne()

        }

        binding.textViewTabProduct.setOnClickListener {
            binding.constraintShopInfo.visibility=View.GONE
            binding.constraintProduct.visibility=View.VISIBLE
            binding.textViewTabProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_round_gray_5dp))
            binding.textViewTabShopInfo.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_round_white_round_5dp))
            binding.pageStepperProduct.setCurrentStepPosition(0)
            productListWithAddBtn()

        }

    }

    private fun addShopInfoStepOne() {
        binding.includeStepOne.constraintShopInfoStepOne.visibility = View.VISIBLE
        binding.includeStepTwo.constraintShopInfoStepTwo.visibility = View.GONE
        binding.includeStepThree.constraintShopInfoStepThree.visibility = View.GONE
        binding.toolbar.toolbarTitle.text = "Upload logo"
    }

    private fun addShopInfoStepTwo() {
        binding.includeStepOne.constraintShopInfoStepOne.visibility = View.GONE
        binding.includeStepTwo.constraintShopInfoStepTwo.visibility = View.VISIBLE
        binding.includeStepThree.constraintShopInfoStepThree.visibility = View.GONE
        binding.toolbar.toolbarTitle.text = "Store detail"
        binding.pageStepper.setCurrentStepPosition(1)
    }

    private fun addShopInfoStepThree() {
        binding.includeStepOne.constraintShopInfoStepOne.visibility = View.GONE
        binding.includeStepTwo.constraintShopInfoStepTwo.visibility = View.GONE
        binding.includeStepThree.constraintShopInfoStepThree.visibility =
            View.VISIBLE
        binding.toolbar.toolbarTitle.text = "Store detail"
        binding.pageStepper.setCurrentStepPosition(2)
    }

    private fun productListWithAddBtn() {
        binding.toolbar.toolbarTitle.text = "Product"
        binding.textViewTabShopInfo.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_round_white_round_5dp))
        binding.textViewTabProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_round_gray_5dp))
        binding.constraintShopInfo.visibility = View.GONE
        binding.pageStepperProduct.visibility = View.GONE
        binding.constraintProduct.visibility = View.VISIBLE
        binding.includeProductList.constraintProductList.visibility = View.VISIBLE
        binding.includeProductStepOne.constraintAddProductStepOne.visibility = View.GONE
        binding.includeProductStepTwo.constraintAddProductStepTwo.visibility = View.GONE
        binding.includeProductStepThree.constraintAddProductStepThree.visibility = View.GONE
        vendorProductList()
    }

    private fun addProductStepOne() {
        binding.includeProductStepOne.constraintAddProductStepOne.visibility = View.VISIBLE
        binding.includeProductStepTwo.constraintAddProductStepTwo.visibility = View.GONE
        binding.includeProductStepThree.constraintAddProductStepThree.visibility = View.GONE
        binding.toolbar.toolbarTitle.text = "Upload image"
        binding.pageStepperProduct.visibility = View.VISIBLE
        binding.includeProductList.constraintProductList.visibility = View.GONE
        addProductStepOneImageSelectionAdapter()
    }

    private fun addProductStepTwo() {
        binding.includeProductStepOne.constraintAddProductStepOne.visibility = View.GONE
        binding.includeProductStepTwo.constraintAddProductStepTwo.visibility = View.VISIBLE
        binding.includeProductStepThree.constraintAddProductStepThree.visibility = View.GONE
        binding.toolbar.toolbarTitle.text = "Product detail"
        binding.pageStepperProduct.setCurrentStepPosition(1)
    }

    private fun addProductStepThree() {
        binding.includeProductStepOne.constraintAddProductStepOne.visibility = View.GONE
        binding.includeProductStepTwo.constraintAddProductStepTwo.visibility = View.GONE
        binding.includeProductStepThree.constraintAddProductStepThree.visibility = View.VISIBLE
        binding.toolbar.toolbarTitle.text = "Product detail"
        binding.pageStepperProduct.setCurrentStepPosition(2)
        addProductColorVariationList()
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun addProductStepOneImageSelectionAdapter()
    {
        val dataList = ArrayList<AddProductImageListModel>()
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_ONE, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))
        dataList.add(AddProductImageListModel(AddProductImageAdapter.VIEW_TYPE_TWO, resources.getDrawable(R.drawable.apple_watch_white)))

        val adapter = AddProductImageAdapter(this, dataList)
        binding.includeProductStepOne.recyclerView.layoutManager = GridLayoutManager(this,3)
        binding.includeProductStepOne.recyclerView.adapter = adapter
    }

    private fun vendorProductList() {
        val similarProductListModel = ArrayList<SimilarProductListModel>()
        var item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Lorem ipsum dolor set met, lorem ipsum dolor set amet.","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)
        item = SimilarProductListModel("TABLE CHAIR SOFA","Lorem ipsum dolor set met, lorem ipsum dolor set amet.","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        item = SimilarProductListModel("BOAT HEADPHONE","Lorem ipsum dolor set met, lorem ipsum dolor set amet.","$520",resources.getDrawable(R.drawable.product_image3))
        similarProductListModel.add(item)
        item = SimilarProductListModel("BOAT HEADPHONE","Lorem ipsum dolor set met, lorem ipsum dolor set amet.","$520",resources.getDrawable(R.drawable.product_image2))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Lorem ipsum dolor set met, lorem ipsum dolor set amet.","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)

        binding.includeProductList.recyclerViewProductList.layoutManager = LinearLayoutManager(this)
        val adapter = VendorProductListAdapter(this, similarProductListModel)
        binding.includeProductList.recyclerViewProductList.adapter = adapter

    }

    private fun addProductColorVariationList() {
        val colorVariationList = ArrayList<DrawerCategoryListModel>()
        var item = DrawerCategoryListModel("#B70C2C")
        colorVariationList.add(item)
        item = DrawerCategoryListModel("#1E1E1E")
        colorVariationList.add(item)
        item = DrawerCategoryListModel("#758FD6")
        colorVariationList.add(item)
        item = DrawerCategoryListModel("#FF9900")
        colorVariationList.add(item)
        binding.includeProductStepThree.recyclerViewColorVariation.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
         val colorVariationAdapter = AddProductColorVariationAdapter(this, colorVariationList)
        binding.includeProductStepThree.recyclerViewColorVariation.adapter = colorVariationAdapter

    }


}