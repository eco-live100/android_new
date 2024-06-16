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
import com.app.ecolive.msg_module.ZegoCallChatActivity
import com.app.ecolive.payment_module.SendMoneyHomePageActivity
import com.app.ecolive.rider_module.HomeRiderActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.adapters.ShopListAdapter
import com.app.ecolive.shop_owner.adapters.ShopOwnerProductListAdapter
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.user_module.ContactListActivity
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.bumptech.glide.Glide
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
            Glide.with(this).load(PreferenceKeeper.instance.loginResponse?.profilePicture).placeholder(R.drawable.ic_user_default).into(binding.include.shopProfile)
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
            binding.include.constraintMakePayment->{ startActivity(
                Intent(
                    this@ShopOwnerHomePageNavigationActivity,
                    SendMoneyHomePageActivity::class.java
                )

            )}
            binding.include.constraintCallFriends->{ startActivity(
                Intent(this@ShopOwnerHomePageNavigationActivity, ContactListActivity::class.java)

            )}
            binding.include.constraintMessage->{startActivity(
                Intent(this@ShopOwnerHomePageNavigationActivity, ZegoCallChatActivity::class.java)

            )}
        }
    }

    private fun riderLoginChk() {
        // startActivity(Intent(this@UserHomePageNavigationActivity, VehicleInfoActivity::class.java))
        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isRider) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(Intent(this@ShopOwnerHomePageNavigationActivity, HomeRiderActivity::class.java))
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


        setTouchNClick(binding.include.shopAddNewStore)
        setTouchNClick(binding.include.toolbar.homepageToolbarSwitchToUser)
        setTouchNClick(binding.include.toolbar.homepageToolbarSwitchToRider)
        setTouchNClick(binding.include.constraintMakePayment)
        setTouchNClick(binding.include.constraintMessage)
        setTouchNClick(binding.include.constraintCallFriends)
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

     private var back_pressed_time: Long = 0
    private val PERIOD: Long = 2000

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        super.onBackPressed()
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

    override fun onOptionSelect(option: String, id: String) {
        if (option == AppConstant.DELETE_KEY) {
            deleteShopApi(id)
            bottomSheetDel.dismiss()
        }
    }

    private fun deleteShopApi(id: String) {
        progressDialog.show(THIS!!)
        var shopViewModel = CommonViewModel(THIS!!)
        shopViewModel.deleteStore(id).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {

                    }
                    vendorShopListAPICAll()

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