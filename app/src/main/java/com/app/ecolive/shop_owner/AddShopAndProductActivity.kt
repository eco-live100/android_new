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

class AddShopAndProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddShopAndProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop_and_product)

        binding.toolbar.ivBack.setOnClickListener {finish()}










    }





}