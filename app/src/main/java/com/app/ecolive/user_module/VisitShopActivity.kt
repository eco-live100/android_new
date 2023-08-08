package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityVisitShopBinding
import com.app.ecolive.localmodel.ListPopupMenu
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.shop_owner.AddResturantItemActivity
import com.app.ecolive.shop_owner.adapters.VisitShopProductListAdapter
import com.app.ecolive.user_module.user_adapter.ListPopupWindowAdapter
import com.app.ecolive.utils.Utils

class VisitShopActivity : AppCompatActivity() {
    lateinit var binding: ActivityVisitShopBinding
    var tabType="all_product"
    lateinit var adapter: VisitShopProductListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_visit_shop)
        statusBarColor()
        initView()
        productList()
        binding.toolbar.toolbarTitle.text="Shop"
        if(intent.extras!=null){
            if (intent.getStringExtra("from").toString()=="grocery"){
                binding.toolbar.toolbarTitle.text="MyGrocery store"
                binding.imageView34.setImageResource(R.drawable.demo_img3)
                binding.textView71.text ="Fresh Grocery"
            }
        }
    }
    private fun initView()
    {
        binding.constraintTabAllProduct.setOnClickListener {
            tabType="all_product"
            filterSelection(binding.constraintTabAllProduct,binding.imageViewTabAllProduct,binding.textViewAllProduct) }

        binding.constraintTabOutOfStock.setOnClickListener {
            tabType="out_of_stock"
            filterSelection( binding.constraintTabOutOfStock,binding.imageViewTabOutOfStock,binding.textViewOutOfStock) }

        binding.productDetailMenu.setOnClickListener { showListPopupWindow(it) }
    }
    private fun filterSelection(
        constraintTab: ConstraintLayout,
        imageViewTab: ImageView,
        textViewTab: TextView
    )
    {
        binding.constraintTabAllProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white_round_tab_50dp))
        binding.constraintTabOutOfStock.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white_round_tab_50dp))
        binding.imageViewTabAllProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_light_gray))
        binding.imageViewTabOutOfStock.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_light_gray))
        binding.textViewAllProduct.setTextColor(resources.getColor(R.color.color_333333))
        binding.textViewOutOfStock.setTextColor(resources.getColor(R.color.color_333333))


        constraintTab.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_blue_round_tab_50dp))
        imageViewTab.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_white))
        textViewTab.setTextColor(resources.getColor(R.color.white))

    }
    private fun statusBarColor() {

        binding.toolbar.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun productList() {
        val similarProductListModel = ArrayList<SimilarProductListModel>()
        var item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch White","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch White With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch White","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch White With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch Gold With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch))
        similarProductListModel.add(item)
        item = SimilarProductListModel("Apple Watch White With Extra Large size","Men's Watch","$520",resources.getDrawable(R.drawable.apple_watch_white))
        similarProductListModel.add(item)
        binding.productListRecyclerview.layoutManager = StaggeredGridLayoutManager(2,
            LinearLayoutManager.VERTICAL)
        adapter = VisitShopProductListAdapter(this, similarProductListModel,object : VisitShopProductListAdapter.ClickListener{
            override fun onClick(pos: Int) {
               // startActivity(Intent(this@VisitShopActivity, ProductDetailActivity::class.java))
            }
        })
        binding.productListRecyclerview.adapter = adapter

    }

    private fun showListPopupWindow(
        anchor: View,
    ) {
        val listPopupItems: MutableList<ListPopupMenu> = java.util.ArrayList()
        listPopupItems.add(ListPopupMenu("Add"))
        listPopupItems.add(ListPopupMenu("Message"))
        listPopupItems.add(ListPopupMenu("Call"))
        listPopupItems.add(ListPopupMenu("Money Transfer"))

        val listPopupWindow: ListPopupWindow = createListPopupWindow(anchor, 370, listPopupItems)
        listPopupWindow.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, pos, id ->
            var id: String = listPopupItems[pos].title
            if(intent.extras!=null){
                if (intent.getStringExtra("from").toString()=="grocery"){
                    if (id=="Add"){
                        startActivity(
                            Intent(
                                this@VisitShopActivity,
                                AddResturantItemActivity::class.java
                            ).putExtra("from","grocery")
                        )
                    }
                }}
            listPopupWindow.dismiss()

        })
        listPopupWindow.show()
    }

    private fun createListPopupWindow(
        anchor: View, width: Int,
        items: List<ListPopupMenu>,
    ): ListPopupWindow {
        val popup = ListPopupWindow(this)
        val adapter: ListAdapter = ListPopupWindowAdapter(items)
        popup.anchorView = anchor
        popup.width = width
        popup.setAdapter(adapter)
        return popup
    }

}