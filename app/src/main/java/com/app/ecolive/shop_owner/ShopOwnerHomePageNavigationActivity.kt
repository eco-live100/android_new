package com.app.ecolive.shop_owner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityShopOwnerHomePageNavigationBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.rider_module.HomeRiderrActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.adapters.ShopListAdapter
import com.app.ecolive.shop_owner.adapters.ShopOwnerProductListAdapter
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.nightout.ui.fragment.BottomSheetDel
import com.offercity.base.BaseActivity

class ShopOwnerHomePageNavigationActivity : BaseActivity() , OnSelectOptionListener{
    lateinit var binding: ActivityShopOwnerHomePageNavigationBinding

    lateinit var adapter: ShopOwnerProductListAdapter
    private var drawerLayout: DrawerLayout? = null
    private val progressDialog = CustomProgressDialog()
    var storeType="Sole Proprietors"
    private lateinit var bottomSheetDel: BottomSheetDel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_owner_home_page_navigation)
        statusBarColor()
        initView()
        vendorShopListAPICAll()
        setLoginData()
        if(PreferenceKeeper.instance.loginResponse==null){
            binding.includeLeftDrawer.sideMenuUserName.text= "Hello"
        }else{
            binding.includeLeftDrawer.sideMenuUserName.text= "Hello, "+PreferenceKeeper.instance.loginResponse?.firstName
        }


    }



    private fun setLoginData() {
        try {
            if(PreferenceKeeper.instance.isUserLogin){
                if(PreferenceKeeper.instance.loginResponse!=null){
                    binding.include.shopUserName.text = PreferenceKeeper.instance.loginResponse?.firstName+" "+PreferenceKeeper.instance.loginResponse?.lastName
                }
            }
        } catch (e: Exception) {
        }
    }


    private fun vendorShopListAPICAll() {
        progressDialog.show(THIS!!)
        var shopViewModel = CommonViewModel(THIS!!)
        shopViewModel.storeList().observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                            setlistStore(it.data)
                        try {
                            storeType=it.data[0].shopType
                        } catch (e: Exception) {
                            storeType= "Sole Proprietors"
                        }
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

    lateinit var shopListAdapter: ShopListAdapter
    private fun setlistStore(dataList: ArrayList<ShopListModel.Data>) {
        shopListAdapter = ShopListAdapter(this@ShopOwnerHomePageNavigationActivity, dataList,object :ShopListAdapter.ClickListener{
            override fun onClick(pos: Int) {
                startActivity(Intent(this@ShopOwnerHomePageNavigationActivity,ProductListActivity::class.java)
                    .putExtra(AppConstant.STORE_DATA,dataList[pos]))
            }

            override fun onSetting(pos: Int) {
                 showBotmSheetSetting(dataList[pos])
            }

        })

        binding.include.shopRecycleShop.also {
            it.layoutManager = LinearLayoutManager(this@ShopOwnerHomePageNavigationActivity,LinearLayoutManager.HORIZONTAL,false)
            it.adapter = shopListAdapter
        }


    }

    private fun showBotmSheetSetting(dataInfo: ShopListModel.Data) {
        bottomSheetDel = BottomSheetDel(this, dataInfo)
        bottomSheetDel.show(THIS!!.supportFragmentManager, "selectSourceBottomSheetFragment")
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.include.toolbar.homepageToolbarSwitchToUser -> {
                startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, UserHomePageNavigationActivity::class.java))
                finish()
            }
            binding.include.toolbar.homepageToolbarSwitchToRider -> {
                riderLoginChk()
            }
            binding.include.shopAddNewStore -> {
                startActivity(Intent(THIS!!,ShopSignupSoleProprietors::class.java)
                    .putExtra(AppConstant.STORE_TYPE,storeType))


            }
        }
    }

    private fun riderLoginChk() {
        // startActivity(Intent(this@UserHomePageNavigationActivity, VehicleInfoActivity::class.java))
        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isRider) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, HomeRiderrActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, VehicleInfoActivity::class.java))
            }
        } else {
            goLoginScreen()
        }
    }

    private fun goLoginScreen() {
        Utils.showMessage(THIS!!, "You have to login first")
        startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, LoginActivity::class.java))
        finish()
    }

    private fun initView() {
        /*   binding.include.constraintTabAllProduct.setOnClickListener {
               tabType="all_product"
               filterSelection(binding.include.constraintTabAllProduct,binding.include.imageViewTabAllProduct,binding.include.textViewAllProduct) }

           binding.include.constraintTabOutOfStock.setOnClickListener {
               tabType="out_of_stock"
               filterSelection( binding.include.constraintTabOutOfStock,binding.include.imageViewTabOutOfStock,binding.include.textViewOutOfStock) }

           binding.include.productDetailMenu.setOnClickListener { showListPopupWindow(it) }
           binding.includeLeftDrawer.homepageDrawerMyOrder.setOnClickListener {
               startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, AcceptedDeclineOrderActivity::class.java))
           }
           binding.include.btnAddProduct.setOnClickListener {
               startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, AddShopAndProductActivity::class.java))
           }*/


        setTouchNClick(binding.include.shopAddNewStore)
        setTouchNClick(binding.include.toolbar.homepageToolbarSwitchToUser)
        setTouchNClick(binding.include.toolbar.homepageToolbarSwitchToRider)
        drawerLayout = binding.drawerLayout
        binding.include.toolbar.ivMenu.setOnClickListener { openCloseNavigationDrawerStart() }

        binding.includeLeftDrawer.sideMenuLogout.setOnClickListener {
            PreferenceKeeper.instance.isUserLogin=false
            PreferenceKeeper.instance.loginResponse = null
            val i = Intent(applicationContext, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("EXIT", true)
            startActivity(i)
            finish()
        }
        setSpinnerSideMenu()
    }

    private fun setSpinnerSideMenu() {
        var list = ArrayList<String>()
        list.add("As a Rider")
        list.add("As a Shop-Owner")
        list.add("As a User")
        val aa: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.includeLeftDrawer.sideMenuSpinner.adapter = aa
        binding.includeLeftDrawer.sideMenuSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

  /*  private fun filterSelection(
        constraintTab: ConstraintLayout,
        imageViewTab: ImageView,
        textViewTab: TextView
    ) {
        binding.include.constraintTabAllProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white_round_tab_50dp))
        binding.include.constraintTabOutOfStock.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_white_round_tab_50dp))
        binding.include.imageViewTabAllProduct.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_light_gray))
        binding.include.imageViewTabOutOfStock.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_light_gray))
        binding.include.textViewAllProduct.setTextColor(resources.getColor(R.color.color_333333))
        binding.include.textViewOutOfStock.setTextColor(resources.getColor(R.color.color_333333))
        constraintTab.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_blue_round_tab_50dp))
        imageViewTab.setBackgroundDrawable(resources.getDrawable(R.drawable.circle_white))
        textViewTab.setTextColor(resources.getColor(R.color.white))

    }*/

    fun openCloseNavigationDrawerStart() {
        when {
            drawerLayout!!.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout!!.closeDrawer(GravityCompat.START)
            }
            else -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)

    }

 /*   private fun productList() {
        val similarProductListModel = ArrayList<SimilarProductListModel>()
        var item = SimilarProductListModel(
            "Apple Watch Gold With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch Gold With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch Gold With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch)
        )
        similarProductListModel.add(item)
        item = SimilarProductListModel(
            "Apple Watch White With Extra Large size",
            "Men's Watch",
            "$520",
            resources.getDrawable(R.drawable.apple_watch_white)
        )
        similarProductListModel.add(item)
        binding.include.productListRecyclerview.layoutManager = StaggeredGridLayoutManager(
            2,
            LinearLayoutManager.VERTICAL
        )
        adapter = ShopOwnerProductListAdapter(
            this,
            similarProductListModel,
            object : ShopOwnerProductListAdapter.ClickListener {
                override fun onClick(pos: Int) {
                    // startActivity(Intent(this@VisitShopActivity, ProductDetailActivity::class.java))
                }

                override fun onMenuClick(it: View, position: Int) {
                    showListPopupWindow1(it)
                }
            })
        binding.include.productListRecyclerview.adapter = adapter

    }

    private fun showListPopupWindow1(anchor: View, ) {
        val listPopupItems: MutableList<ListPopupMenu> = java.util.ArrayList()
        listPopupItems.add(ListPopupMenu("Edit"))
        listPopupItems.add(ListPopupMenu("Change"))
        listPopupItems.add(ListPopupMenu("Delete"))

        val listPopupWindow: ListPopupWindow = createListPopupWindow(anchor, 370, listPopupItems)
        listPopupWindow.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, pos, id ->
            var id: String = listPopupItems[pos].title
            listPopupWindow.dismiss()

        })
        listPopupWindow.show()
    }

    private fun showListPopupWindow(anchor: View, ) {
        val listPopupItems: MutableList<ListPopupMenu> = java.util.ArrayList()
        listPopupItems.add(ListPopupMenu("Message"))
        listPopupItems.add(ListPopupMenu("Call"))
        listPopupItems.add(ListPopupMenu("Money Transfer"))

        val listPopupWindow: ListPopupWindow = createListPopupWindow(anchor, 370, listPopupItems)
        listPopupWindow.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, pos, id ->
            var id: String = listPopupItems[pos].title
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
*/
    private var back_pressed_time: Long = 0
    private val PERIOD: Long = 2000

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (back_pressed_time + PERIOD > System.currentTimeMillis())
            finishAffinity()
        else {
            if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout!!.closeDrawer(GravityCompat.START)
            } else {
                Utils.showMessage(
                    this@ShopOwnerHomePageNavigationActivity,
                    getResources().getString(R.string.press_again)
                )
                back_pressed_time = System.currentTimeMillis()
            }
        }
    }

    override fun onOptionSelect(option: String) {
        if (option == AppConstant.DELETE_KEY) {
            bottomSheetDel.dismiss()
        }
    }
}