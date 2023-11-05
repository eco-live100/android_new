package com.app.ecolive.rider_module

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.common_screen.adapters.DrawerCategoryListAdapter
import com.app.ecolive.common_screen.adapters.HomeCategoryListAdapter
import com.app.ecolive.common_screen.adapters.HomeProductListAdapter
import com.app.ecolive.databinding.HomeriderActivityBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.msg_module.ChatListActivity
import com.app.ecolive.payment_module.AddMoneyMainActivity
import com.app.ecolive.payment_module.SendMoneyHomePageActivity
import com.app.ecolive.payment_module.UserVerificationAddMoneyActivity
import com.app.ecolive.rider_module.adapter.RiderHomeOrderAdapter
import com.app.ecolive.rider_module.model.RiderHomeOrderModel
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.shop_owner.ShopUserSignupActivityNew
import com.app.ecolive.taximodule.AboutStartTripActivity
import com.app.ecolive.user_module.*
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.GoogleMap
import com.localmerchants.ui.localModels.DrawerCategoryListModel
import com.nightout.ui.fragment.SearchLocBottomSheet
import com.offercity.base.BaseActivity


class HomeRiderActivity : BaseActivity()  {
    lateinit var binding: HomeriderActivityBinding
    var adapter: HomeCategoryListAdapter? = null
    var adapterCategory: DrawerCategoryListAdapter? = null
    private var mMap: GoogleMap? = null
    private val drawerCategoryListModel = ArrayList<DrawerCategoryListModel>()
    var adapterProduct: HomeProductListAdapter? = null
    private var drawerLayout: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.homerider_activity)
        initView()
        if(PreferenceKeeper.instance.loginResponse==null){
            binding.includeLeftDrawer.sideMenuUserName.text= "Hello User"
        }else{
            binding.includeLeftDrawer.sideMenuUserName.text= "Hello, "+PreferenceKeeper.instance.loginResponse?.firstName
        }
        var user = PreferenceKeeper.instance.loginResponse
        if (user != null) {
            binding.include.contentHome.riderUserName.text ="${user.firstName} ${user.lastName}"
            //Glide.with(this).load(user.profilePicture).into(binding.include.contentHome.riderUserPic)
        }
        setDummyList()


    }

    lateinit var riderHomeOrderAdapter: RiderHomeOrderAdapter
    private fun setDummyList() {
    var listHome  =  ArrayList<RiderHomeOrderModel>()
        listHome.add(RiderHomeOrderModel("Jenny Wilson",R.drawable.dummy_male_user,true))
        listHome.add(RiderHomeOrderModel("Jenny Wilson",R.drawable.dummy_male_user,true))
        listHome.add(RiderHomeOrderModel("Jenny Wilson",R.drawable.dummy_female_user,false))
        listHome.add(RiderHomeOrderModel("Jenny Wilson",R.drawable.dummy_male_user,false))


        riderHomeOrderAdapter = RiderHomeOrderAdapter(this@HomeRiderActivity,listHome,object:RiderHomeOrderAdapter.ClickListener{
            override fun onClik(pos: Int) {
                startActivity(Intent(this@HomeRiderActivity,RideDetailActivity::class.java))

            }


        })

        binding.include.contentHome.riderRecycle.also {
            it.layoutManager = LinearLayoutManager(THIS!!,LinearLayoutManager.VERTICAL,false)
            it.adapter = riderHomeOrderAdapter
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        if(v==binding.include.contentHome.headerHome.homepageToolbarSwitchToUser){
            startActivity(Intent(this@HomeRiderActivity, UserHomePageNavigationActivity::class.java))
            finish()
           // riderLoginChk()

        }
        else if(v ==  binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor){
           shopLoginChk()
        }
        else if(v==binding.include.contentHome.riderChkDemand){
            startActivity(Intent(this@HomeRiderActivity,CheckDemandActivity::class.java))
        }else if(v==binding.include.contentHome.starttrip){
            startActivity(Intent(this@HomeRiderActivity,AboutStartTripActivity::class.java))
        }

    }
    private fun goLoginScreen() {
        Utils.showMessage(THIS!!,"You have to login first")
        startActivity(Intent(this@HomeRiderActivity, LoginActivity::class.java))
        finish()
    }
    private fun shopLoginChk() {
        if(PreferenceKeeper.instance.loginResponse!=null){
            if(PreferenceKeeper.instance.loginResponse!!.isVendor){
              //  MyApp.popErrorMsg("","Your Shop details is in under verification",THIS!!)
               startActivity(Intent(this@HomeRiderActivity, ShopOwnerHomePageNavigationActivity::class.java))
            }else{
                startActivity(Intent(this@HomeRiderActivity, ShopUserSignupActivityNew::class.java))
            }
        }else{
            goLoginScreen()
        }
    }

    private fun riderLoginChk() {
        if(PreferenceKeeper.instance.loginResponse!=null){
            if(PreferenceKeeper.instance.loginResponse!!.isRider){
               // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                   startActivity(Intent(this@HomeRiderActivity, CurrentOrderListActivity::class.java))
            }else{
                startActivity(Intent(this@HomeRiderActivity, VehicleInfoActivity::class.java))
            }
        }
        else{
            goLoginScreen()
        }
    }


    private fun initView() {
        statusBarColor()

        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToUser)
        setTouchNClick( binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor)
        setTouchNClick( binding.include.contentHome.riderChkDemand)
        setTouchNClick( binding.include.contentHome.starttrip)

        drawerLayout = binding.drawerLayout
        binding.include.contentHome.headerHome.ivMenu.setOnClickListener { openCloseNavigationDrawerStart() }




        sideMenuCategoryList()

        setSpinnerSideMenu()


        binding.includeLeftDrawer.homepageDrawerMyAccount.setOnClickListener {
            startActivity(Intent(this@HomeRiderActivity, MyAccountActivity::class.java))
        }

        binding.includeLeftDrawer.homepageDrawerHome.setOnClickListener {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }


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



        binding.includeLeftDrawer.homepageDrawerMyOrder.setOnClickListener {
            startActivity(Intent(this@HomeRiderActivity, MyOrderActivity2::class.java))
        }
        binding.includeLeftDrawer.homepageDrawerMyAccount.visibility=View.VISIBLE
        binding.includeLeftDrawer.view6.visibility=View.VISIBLE

        binding.include.constraintSendMoney.setOnClickListener {
            if(PreferenceKeeper.instance.loginResponse==null){
                goLoginScreen()
            }else {
                startActivity(
                    Intent(
                        this@HomeRiderActivity,
                        SendMoneyHomePageActivity::class.java
                    )
                )
            }
        }

        binding.include.constraintAddMoney.setOnClickListener {
            if(PreferenceKeeper.instance.loginResponse==null){
                goLoginScreen()
            }else {
                startActivity(
                    Intent(
                        this@HomeRiderActivity,
                        UserVerificationAddMoneyActivity::class.java
                    )
                )
            }

        }

        binding.include.constraintMakePayment.setOnClickListener {
            if(PreferenceKeeper.instance.loginResponse==null){
                goLoginScreen()
            }else {
                startActivity(Intent(this@HomeRiderActivity, AddMoneyMainActivity::class.java)
                    .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME,true))
            }

        }



        binding.include.constraintCallFriends.setOnClickListener{
            if(PreferenceKeeper.instance.loginResponse==null){
                goLoginScreen()
            }else {
                startActivity(Intent(this@HomeRiderActivity, ContactListActivity::class.java)
                    .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME,true))
            }

        }
        binding.include.constraintMessage.setOnClickListener{
            if(PreferenceKeeper.instance.loginResponse==null){
                goLoginScreen()
            }else {
                startActivity(Intent(this@HomeRiderActivity, ChatListActivity::class.java)
                    .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME,true))
            }

        }

    }

    private fun setSpinnerSideMenu() {
        var list = ArrayList<String>()
        list.add("As a User")
        list.add("As a Rider")
        list.add("As a Shop-Owner")

        val aa: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.includeLeftDrawer.sideMenuSpinner.adapter=aa
        binding.includeLeftDrawer.sideMenuSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if(position==0){

                    }
                    else if(position==1){
                        startActivity(Intent(this@HomeRiderActivity, UserHomePageNavigationActivity::class.java))
                        finish()
                         //   riderLoginChk()
                    }
                    else if(position==2){
                            shopLoginChk()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    lateinit var secrachLocationBtmSheet: SearchLocBottomSheet
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
    fun openCloseNavigationDrawerEnd() {
        when {
            drawerLayout!!.isDrawerOpen(GravityCompat.END) -> {
                drawerLayout!!.closeDrawer(GravityCompat.END)
            }
            else -> {
                drawerLayout!!.openDrawer(GravityCompat.END)
            }
        }
    }





    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
       // Utils.changeStatusTextColor(this)
    }



    private fun sideMenuCategoryList()
    {
        var item1 = DrawerCategoryListModel("Fashion & Beauty")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Electronics and Devices")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Home & diy")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Office & Professional")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Automotive")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Toys")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Kids & Babies")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Music")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Games & Videos")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Book & Readins pets")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Drugstore & Personal care")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Groceries & Drinks")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Sports and Outdoors")
        drawerCategoryListModel.add(item1)
        item1 = DrawerCategoryListModel("Others")
        drawerCategoryListModel.add(item1)



    }



    private var back_pressed_time: Long = 0
    private val PERIOD: Long = 2000
    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (back_pressed_time + PERIOD > System.currentTimeMillis())
            finishAffinity()
        else {
            if(drawerLayout!!.isDrawerOpen(GravityCompat.START)){
                drawerLayout!!.closeDrawer(GravityCompat.START)
            } else {
                Utils.showMessage(this@HomeRiderActivity,getResources().getString(R.string.press_again))
                back_pressed_time = System.currentTimeMillis()
            }
        }
    }



}