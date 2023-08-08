package com.app.ecolive.shop_owner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAddProductImageDummyBinding
import com.app.ecolive.databinding.ActivityShopOwnerHomePageNavigationBinding
import com.app.ecolive.localmodel.AddProductImageListModel
import com.app.ecolive.shop_owner.adapters.AddProductImageAdapter

class AddProductImageDummyActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddProductImageDummyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_image_dummy)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product_image_dummy)
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
        binding.recyclerView.layoutManager = GridLayoutManager(this,3)
        binding.recyclerView.adapter = adapter
    }
}