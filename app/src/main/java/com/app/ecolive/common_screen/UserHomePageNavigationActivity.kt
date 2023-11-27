package com.app.ecolive.common_screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.DrawerCategoryListAdapter
import com.app.ecolive.common_screen.adapters.HomeCategoryListAdapter
import com.app.ecolive.common_screen.adapters.HomeProductListAdapter
import com.app.ecolive.databinding.ActivityUserHomePageNavigationBinding
import com.app.ecolive.localmodel.HomeProductListModel
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.msg_module.CallActivity
import com.app.ecolive.msg_module.ChatListActivity
import com.app.ecolive.msg_module.CometChatInterface
import com.app.ecolive.msg_module.cometchat
import com.app.ecolive.payment_module.SelectPaymentAction
import com.app.ecolive.payment_module.SendMoneyHomePageActivity
import com.app.ecolive.pharmacy_module.PharmacyOptionActivity
import com.app.ecolive.rider_module.HomeRiderActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.shop_owner.ShopUserSignupActivityNew
import com.app.ecolive.shop_owner.model.ProductModel
import com.app.ecolive.taximodule.TaxiHomeActivity
import com.app.ecolive.user_module.*
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.localmerchants.ui.localModels.DrawerCategoryListModel
import com.nightout.ui.fragment.SearchLocBottomSheet
import com.offercity.base.BaseActivity
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import org.json.JSONObject
import java.util.*


class UserHomePageNavigationActivity : BaseActivity(), OnMapReadyCallback, CometChatInterface,
    OnSelectOptionListener {

    lateinit var binding: ActivityUserHomePageNavigationBinding
    var adapter: HomeCategoryListAdapter? = null
    var adapterCategory: DrawerCategoryListAdapter? = null
    private var mMap: GoogleMap? = null
    private val drawerCategoryListModel = ArrayList<DrawerCategoryListModel>()
    var adapterProduct: HomeProductListAdapter? = null
    private var drawerLayout: DrawerLayout? = null
    private val progressDialog = CustomProgressDialog()
    val scanCustomCode = registerForActivityResult(ScanCustomCode(), ::handleResult)

    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_home_page_navigation)
        initView()
        if (PreferenceKeeper.instance.loginResponse == null) {
            binding.includeLeftDrawer.sideMenuUserName.text = "Hello"
        } else {
            binding.includeLeftDrawer.sideMenuUserName.text =
                "Hello, " + PreferenceKeeper.instance.loginResponse?.firstName
        }

        if (PreferenceKeeper.instance.isUserLogin) {
            binding.include.contentHome.headerHome.homepageToolbarLogin.visibility = GONE
        }
        val uid = ""+PreferenceKeeper.instance.loginResponse?._id // Replace with the UID for the user to be created

        cometchat.login(uid)
        cometchat.recivedCall(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest?.interval = 4000
        locationRequest?.fastestInterval = 2000
        locationRequest?.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult
                for (location in locationResult.locations) {
                    MyApp.locationLast = location
                    Log.d(
                        "TAG",
                        "UserHome onLocationResult: " + location.latitude + "\n" + location.longitude
                    )
                }
            }
        }
        getLocation()

    }

    override fun onGotoCall() {
        startActivity(Intent(this, CallActivity::class.java) )
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v == binding.include.contentHome.headerHome.homepageToolbarSwitchToRider) {
            // startActivity(Intent(this@UserHomePageNavigationActivity, VehicleInfoActivity::class.java))
            showPOPUP()

        } else if (v == binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor) {
            shopLoginChk()
        }

    }

    private fun showPOPUP() {
        PopUpVehicleChoose.getInstance().createDialog(
            this@UserHomePageNavigationActivity,
            "",
            object : PopUpVehicleChoose.Dialogclick {
                override fun onYes() {
                    riderLoginChk()
                }

                override fun onNo() {

                }

                override fun onPedesstrain() {

                }

                override fun onCycle() {

                }

                override fun onElectric() {

                }

                override fun onPetrol() {

                }

                override fun onBio() {

                }

            })
    }

    private fun goLoginScreen() {
        Utils.showMessage(THIS!!, resources.getString(R.string.you_login_first))
        startActivity(Intent(this@UserHomePageNavigationActivity, LoginActivity::class.java))
        finish()
    }

    private fun shopLoginChk() {
        // startActivity(Intent(this@UserHomePageNavigationActivity, ShopOwnerHomePageNavigationActivity::class.java))

        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isVendor) {
                //  MyApp.popErrorMsg("","Your Shop details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        ShopOwnerHomePageNavigationActivity::class.java
                    )
                )
            } else {
                shopRegister()
            }
        } else {
            goLoginScreen()
        }
        // startActivity(Intent(this@UserHomePageNavigationActivity, ShopUserSignupActivityNew::class.java))
        //startActivity(Intent(this@UserHomePageNavigationActivity, ShopSignupSoleProprietors::class.java))

    }

    private fun riderLoginChk() {
        // startActivity(Intent(this@UserHomePageNavigationActivity, HomeRiderrActivity::class.java))

        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isRider) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        HomeRiderActivity::class.java
                    )
                )
                finish()
            } else {
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        VehicleInfoActivity::class.java
                    )
                )
            }
        } else {
            goLoginScreen()
        }
    }


    private fun initView() {
        statusBarColor()
        homeCategoryList()
        vendorShopProductListAPICAll()

        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToRider)
        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.homeMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        drawerLayout = binding.drawerLayout

        binding.include.contentHome.headerHome.ivMenu.setOnClickListener {
            try {
                Utils.hideSoftKeyBoard(this@UserHomePageNavigationActivity)
            } catch (e: Exception) {
            }
            openCloseNavigationDrawerStart()
        }


        binding.include.contentHome.headerHome.homepageToolbarLogin.setOnClickListener {
            startActivity(
                Intent(this@UserHomePageNavigationActivity, LoginActivity::class.java)
                    .putExtra(
                        AppConstant.INTENT_EXTRAS.INTENT_TYPE,
                        AppConstant.INTENT_VALUE.INTENT_TYPE_GET_STARTED
                    )
            )
            finish()
        }

        binding.include.contentHome.tvSelectCategory.setOnClickListener { openCloseNavigationDrawerEnd() }
        sideMenuCategoryList()

        setSpinnerSideMenu()


        binding.includeLeftDrawer.homepageDrawerMyAccount.setOnClickListener {
            try {
                Utils.hideSoftKeyBoard(this@UserHomePageNavigationActivity)
            } catch (e: Exception) {
            }
            startActivity(
                Intent(
                    this@UserHomePageNavigationActivity,
                    MyAccountActivity::class.java
                )
            )
        }

        binding.includeLeftDrawer.homepageDrawerHome.setOnClickListener {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }


        binding.includeLeftDrawer.sideMenuLogout.setOnClickListener {
            PreferenceKeeper.instance.isUserLogin = false
            PreferenceKeeper.instance.loginResponse = null
            val i = Intent(applicationContext, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("EXIT", true)
            startActivity(i)
            cometchat.logout()
            finish()
        }



        binding.includeLeftDrawer.homepageDrawerMyOrder.setOnClickListener {
            startActivity(Intent(this@UserHomePageNavigationActivity, MyOrderActivity2::class.java))
        }

        if (PreferenceKeeper.instance.loginResponse == null) {
            binding.includeLeftDrawer.homepageDrawerMyAccount.visibility = View.GONE
            binding.includeLeftDrawer.homepageDrawerMyOrder.visibility = View.GONE
        } else {
            binding.includeLeftDrawer.homepageDrawerMyAccount.visibility = View.VISIBLE
            binding.includeLeftDrawer.homepageDrawerMyOrder.visibility = View.VISIBLE
        }



        binding.includeLeftDrawer.view6.visibility = View.VISIBLE

        binding.include.constraintSendMoney.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        SendMoneyHomePageActivity::class.java
                    )
                )
            }
        }

        binding.include.constraintTaxi.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        TaxiHomeActivity::class.java
                    )
                )
            }

        }

        binding.include.constraintMakePayment.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
               /* startActivity(
                    Intent(this@UserHomePageNavigationActivity, AddMoneyMainActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )*/
                startActivity(
                    Intent(this@UserHomePageNavigationActivity, SelectPaymentAction::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }

        }



        binding.include.constraintCallFriends.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(this@UserHomePageNavigationActivity, ContactListActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }

        }

        binding.include.constraintMessage.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(this@UserHomePageNavigationActivity, ChatListActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }

        }

        binding.include.contentHome.etSearchLocation.setOnClickListener {
            secrachLocationBtmSheet = SearchLocBottomSheet(object :OnSelectOptionListener{
                override fun onOptionSelect(location: String) {
                    Toast.makeText(THIS, ""+location.toString(), Toast.LENGTH_SHORT).show()
                }
            }, "")
            secrachLocationBtmSheet.show(
                this@UserHomePageNavigationActivity.supportFragmentManager,
                "SEARCHLOCATION"
            )
        }

        binding.include.contentHome.showmoreProduct.setOnClickListener {
            startActivity(
                Intent(this@UserHomePageNavigationActivity, ProductListActivity::class.java)
                    .putExtra(AppConstant.CATEGORY, AppConstant.RETAIL)
            )
        }

        binding.include.contentHome.mapInformation.setOnClickListener {
            showDialog()
        }

        binding.include.contentHome.DeliveryJobMap.setOnClickListener {
            showPOPUP()
        }

        binding.include.contentHome.headerHome.qrCodeScannerImg.setOnClickListener {
            scanCustomCode.launch(
                ScannerConfig.build {
                    setBarcodeFormats(listOf(BarcodeFormat.FORMAT_ALL_FORMATS)) // set interested barcode formats
                    setOverlayStringRes(R.string.scan_barcode) // string resource used for the scanner overlay
                    setOverlayDrawableRes(R.drawable.app_logo_bgtrans) // drawable resource used for the scanner overlay
                    setHapticSuccessFeedback(false) // enable (default) or disable haptic feedback when a barcode was detected
                    setShowTorchToggle(true) // show or hide (default) torch/flashlight toggle button
                    setHorizontalFrameRatio(1.2f) // set the horizontal overlay ratio (default is 1 / square frame)
                    setUseFrontCamera(false) // use the front camera
                }
            )
        }
    }

    fun handleResult(result: QRResult) {

    }

    private fun setSpinnerSideMenu() {
        var list = ArrayList<String>()
        list.add("As a User")
        list.add("As a Rider")
        list.add("As a Shop-Owner")

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
                    when (position) {
                        0 -> {

                        }
                        1 -> {
                            riderLoginChk()
                        }
                        2 -> {
                            shopLoginChk()
                        }
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

    private fun homeCategoryList() {
        shopCategoriesListAPICAll()
    }

    private fun shopCategoriesListAPICAll() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        loginViewModel.shopCategoriesList().observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        binding.include.contentHome.categoryRecyclerview.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        adapter = HomeCategoryListAdapter(
                            this,
                            it.data,
                            object : HomeCategoryListAdapter.ClickListener {
                                override fun viewProductList(pos: Int) {
                                    if (pos == 0) {
                                        startActivity(
                                            Intent(
                                                this@UserHomePageNavigationActivity,
                                                TaxiHomeActivity::class.java
                                            )
                                        )
                                    } else if (pos == 1) {
                                        startActivity(
                                            Intent(
                                                this@UserHomePageNavigationActivity,
                                                ProductListActivity::class.java
                                            )
                                                .putExtra(AppConstant.CATEGORY, AppConstant.FOOD)
                                        )

                                    } else if (pos == 2) {
                                        startActivity(
                                            Intent(
                                                this@UserHomePageNavigationActivity,
                                                ProductListActivity::class.java
                                            )
                                                .putExtra(AppConstant.CATEGORY, AppConstant.GROCERY)
                                        )

                                    } else if(pos==3) {
                                        /*if(!PreferenceKeeper.instance.isHealthProfileCreate){*/
                                            startActivity(
                                                Intent(
                                                    this@UserHomePageNavigationActivity,
                                                    PharmacyOptionActivity::class.java
                                                )
                                                   .putExtra(
                                                        AppConstant.CATEGORY,
                                                        AppConstant.PHARMACY
                                                    )
                                            )
//                                        }else{
//                                            startActivity(Intent(this@UserHomePageNavigationActivity, PharmacyStepActivity::class.java))
//                                        }

                                    }else{
                                        startActivity(
                                            Intent(
                                                this@UserHomePageNavigationActivity,
                                                ProductListActivity::class.java
                                            )
                                                .putExtra(
                                                    AppConstant.CATEGORY,
                                                    AppConstant.RETAIL
                                                )
                                        )
                                    }
                                }
                            })
                        binding.include.contentHome.categoryRecyclerview.adapter = adapter

                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, THIS!!)
                }
            }
        }
    }

    private fun homeProductList(data: ProductModel.Data) {

        val productList = ArrayList<HomeProductListModel>()
        val productList2 = ArrayList<HomeProductListModel>()
        ///Find here

        for( i in 0 until data.docs.size){
            try {
                var item = HomeProductListModel(
                    data.docs[i].productData.productName,
                    data.docs[i].productData.price,
                    data.docs[i].productData.priceLive,
                    resources.getDrawable(R.drawable.product_image3),
                    data.docs[i].file[0].name,
                    data.docs[i]._id,
                )
                productList.add(item)
                productList2.add(item)
            }catch (e :Exception){

            }



        }

        binding.include.contentHome.productRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterProduct = HomeProductListAdapter(
            this,
            productList,
            object : HomeProductListAdapter.ClickListener {
                override fun viewProductDetails(productId: String) {
                    startActivity(
                        Intent(
                            this@UserHomePageNavigationActivity,
                            ProductDetailActivity::class.java
                        ).putExtra("productId",productId)
                    )
                }
            })
        binding.include.contentHome.productRecyclerview.adapter = adapterProduct


       val adapterProduct2 = HomeProductListAdapter(
            this,
            productList2,
            object : HomeProductListAdapter.ClickListener {
                override fun viewProductDetails(productId: String) {
                    startActivity(
                        Intent(
                            this@UserHomePageNavigationActivity,
                            ProductDetailActivity::class.java
                        ).putExtra("productId",productId)
                    )
                }
            })
        binding.include.contentHome.productRecyclerview2.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.include.contentHome.productRecyclerview2.adapter = adapterProduct2

        binding.include.contentHome.crossofmap.setOnClickListener {
            binding.include.contentHome.textView16.visibility =View.GONE
            binding.include.contentHome.mapCard.visibility =View.GONE
            binding.include.contentHome.productRecyclerview2.visibility =View.VISIBLE
        }

    }

    private fun vendorShopProductListAPICAll() {
       // progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()
        json.put("categoryId","6351876d9c5b36484345bda6" )
        Log.d("ok", "addProductAPICall: "+json)
        addProductViewModel.vendorShopProductList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                  //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        homeProductList(it.data)
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                  //  progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" +vv, THIS!!)
                }
            }
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        // Utils.changeStatusTextColor(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            mMap = googleMap
            // mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMapToolbarEnabled = false
            mMap!!.uiSettings.isMyLocationButtonEnabled = false
            //  googleMap!!.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))//set night mode
            /*  if (ActivityCompat.checkSelfPermission(
                      this,
                      Manifest.permission.ACCESS_FINE_LOCATION
                  ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                      this,
                      Manifest.permission.ACCESS_COARSE_LOCATION
                  ) != PackageManager.PERMISSION_GRANTED
              ) {
                  return
              }*/
            /* val sydney = LatLng(26.780102748020084, 75.46720735380768)
             mMap!!.addMarker(
                 MarkerOptions().position(sydney)
                     .title("Marker in Sydney") // below line is use to add custom marker on our map.
                     .icon(BitmapFromVector(applicationContext, R.drawable.marker_club))
             )
             mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

            //setUpMarker()
            val latLng = LatLng(28.4747789, 77.0419619)
            zoomMapInitial(latLng)
        } catch (e: Exception) {
        }//28.47278,77.0393223
    }

    private fun zoomMapInitial(latLang: LatLng) {
        try {
            /*    val padding = 10
                val bc = LatLngBounds.Builder()

                bc.include(latLang);*/
            val zoomLevel = 14.0f
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, zoomLevel));
            // mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding))


            val latLng1 = LatLng(28.4747789, 77.0419619)
            val latLng2 = LatLng(28.47278, 77.0393223)
            val latLng3 = LatLng(28.4726242, 77.0392608)
            mMap!!.addMarker(
                MarkerOptions().flat(true).position(latLng1)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green1))
            )

            mMap!!.addMarker(
                MarkerOptions().flat(true).position(latLng3)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow1))
            )
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun sideMenuCategoryList() {
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


        val recyclerView = binding.includeRightDrawer.homepageDrawerCategoryRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterCategory = DrawerCategoryListAdapter(
            this@UserHomePageNavigationActivity,
            drawerCategoryListModel
        )
        recyclerView.adapter = adapterCategory
    }

    override fun onOptionSelect(option: String) {
        when (option) {
//            resources.getString(R.string.bid_new) -> {
//                bidlistAPICall(option)
//            }
        }

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
                    this@UserHomePageNavigationActivity,
                    getResources().getString(R.string.press_again)
                )
                back_pressed_time = System.currentTimeMillis()
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_layout_map_info)
        dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER);
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
        val yesBtn = dialog.findViewById(R.id.ok) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun shopRegister() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_shop_register)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        val yesBtn = dialog.findViewById(R.id.ok) as TextView

        yesBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@UserHomePageNavigationActivity,
                    ShopUserSignupActivityNew::class.java
                )
            )
        }
        dialog.show()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        getAddress(location.latitude,location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun getAddress(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.ENGLISH)
            if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(
                    latitude, longitude, 1
                ) { addresses ->
                    print("asdfsafsadf1 ${addresses[0].getAddressLine(0)}")
                    MyApp.lastLocationAddress = addresses[0].getAddressLine(0).toString()
                    print("asdfsafsadf2 ${addresses[0].getAddressLine(0)}")
                }
            } else {
                val list: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                MyApp.lastLocationAddress = list?.get(0)?.getAddressLine(0).toString()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            123
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationRequest?.let {
            mFusedLocationClient.requestLocationUpdates(
                it,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

}