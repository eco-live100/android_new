package com.app.ecolive.rider_module

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.common_screen.adapters.HomeCategoryListAdapter
import com.app.ecolive.databinding.CustomRequestDialogBinding
import com.app.ecolive.databinding.HomeriderActivityBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.msg_module.ChatListActivity
import com.app.ecolive.notification.NotificationModel
import com.app.ecolive.payment_module.AddMoneyMainActivity
import com.app.ecolive.payment_module.SendMoneyHomePageActivity
import com.app.ecolive.payment_module.UserVerificationAddMoneyActivity
import com.app.ecolive.rider_module.adapter.RiderOrderListAdapter
import com.app.ecolive.service.Status
import com.app.ecolive.services.LocationService
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.shop_owner.ShopUserSignupActivityNew
import com.app.ecolive.taximodule.AboutStartTripActivity
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.user_module.*
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.localmerchants.ui.localModels.DrawerCategoryListModel
import com.offercity.base.BaseActivity
import org.json.JSONObject


class HomeRiderActivity : BaseActivity() {
    private lateinit var binding: HomeriderActivityBinding
    var adapter: HomeCategoryListAdapter? = null
    private val drawerCategoryListModel = ArrayList<DrawerCategoryListModel>()
    private var drawerLayout: DrawerLayout? = null
    private val progressDialog = CustomProgressDialog()
    lateinit var dialog: Dialog
    private var isRiderOnline: Boolean = PreferenceKeeper.instance.isDriverOnline


    var polyline: Polyline? = null
    private lateinit var startLatLng: LatLng
    private lateinit var endLatLng: LatLng
    var totalDistance: Double = 0.0
    var totalDuration: Double = 0.0
    private lateinit var riderOrderListAdapter: RiderOrderListAdapter

   private var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
private var notificationModel : NotificationModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.homerider_activity)
        initView()

        dialog = Dialog(this)
        if (PreferenceKeeper.instance.loginResponse == null) {
            binding.includeLeftDrawer.sideMenuUserName.text = getString(R.string.hello_user)
        } else {
            binding.includeLeftDrawer.sideMenuUserName.text =
                "Hello, " + PreferenceKeeper.instance.loginResponse?.firstName
        }
        val user = PreferenceKeeper.instance.loginResponse
        if (user != null) {
            binding.include.contentHome.riderUserName.text =
                "${user.firstName.replaceFirstChar { it.uppercase() }} ${user.lastName}"
            //Glide.with(this).load(user.profilePicture).into(binding.include.contentHome.riderUserPic)
        }
        MyApp.driverlocation = null

        notificationModel =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    AppConstant.notificationModel,
                    NotificationModel::class.java
                )
            } else {
                intent.getSerializableExtra(AppConstant.notificationModel) as NotificationModel
            }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.interval = 4000
        locationRequest?.fastestInterval = 2000
        locationRequest?.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult
                for (location in locationResult.locations) {
                    MyApp.driverlocation = location
                    Log.d(
                        "TAG",
                        "HomeRiderActivity onLocationResult: " + location.latitude + "\n" + location.longitude
                    )
                }
            }
        }
        riderSwitchButton()
        binding.include.contentHome.riderRecycle.layoutManager = LinearLayoutManager(this)
        getProfileAPI()
    }
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data?.getIntExtra("refresh", 0) == 1) {
                    notificationModel = null
                    getProfileAPI()
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
/*    private fun checkPermissions(): Boolean {
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
               // getLocation()
            }
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }*/
/*    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        MyApp.driverlocation = location
                        //getAddress(location.latitude,location.longitude)
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
    }*/

    private fun riderRequestPopUp(
        riderId: String,
        notificationModel: NotificationModel
    ) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val customRequestDialogBinding : CustomRequestDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.custom_request_dialog, null, false)
        dialog.setContentView(customRequestDialogBinding.root);
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       // val mDialogNo = dialog.findViewById<ImageView>(R.id.frmNo)
       /* startLatLng = LatLng(26.8775, 75.8822254)
        endLatLng = LatLng(26.8947446, 75.8301169)*/

        notificationModel.let {
            customRequestDialogBinding.apply {
                pDetailUserName.text="${it.userName.capitalize()}"
                pDetailUserKm.text="${it.distanceInKm}KM"
                bookingIdTv.text="Booking Id:- ${it.bookingNumber}"
                //bookingDateTv.text="${it.createdAt}"
                fromAddressTv.text="${it.fromAddress}"
                toAddressTv.text="${it.toAddress}"
                tvTotBill.text="Total Bill:- $ ${it.amount}"
            }
            startLatLng = LatLng(it.fromLatitude.toDouble(), it.fromLongitude.toDouble())
            endLatLng = LatLng(it.toLatitude.toDouble(), it.toLongitude.toDouble())
        }

        (supportFragmentManager.findFragmentById(R.id.pDetailMap) as SupportMapFragment?)?.getMapAsync { googleMap ->
            onMapReady(googleMap)
        }
        customRequestDialogBinding.frmNo.setOnClickListener {
            dialog.dismiss()
        }
        customRequestDialogBinding.pDetailAccept.setOnClickListener {
            if (isRiderOnline) {
                if(MyApp.driverlocation==null){
                    Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show()
                }else {
                    acceptAPI(
                        riderId = riderId,
                        bookingNumber = notificationModel.bookingNumber,
                        latitude = MyApp.driverlocation?.latitude.toString(),
                        longitude = MyApp.driverlocation?.longitude.toString()
                    )
                }
            }else{

            }

        }
        customRequestDialogBinding.pDetailDecline.setOnClickListener {
            if(MyApp.driverlocation==null){
                Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show()
            }else {
                declineAPI(
                    riderId = riderId,
                    bookingNumber = notificationModel.bookingNumber,
                    latitude = MyApp.driverlocation?.latitude.toString(),
                    longitude = MyApp.driverlocation?.longitude.toString()
                )
            }
        }
        customRequestDialogBinding.pDetailComplete.setOnClickListener {
            if(MyApp.driverlocation==null){
                Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show()
            }else {
                completeAPI(
                    riderId = riderId,
                    bookingNumber = notificationModel.bookingNumber,
                    latitude = MyApp.driverlocation?.latitude.toString(),
                    longitude = MyApp.driverlocation?.longitude.toString()
                )
            }
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        locationServiceForRiderDuty()
    }

    private fun onMapReady(mMap: GoogleMap) {
        val builder = LatLngBounds.Builder()

        builder.include(startLatLng)
        builder.include(endLatLng)

        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap.animateCamera(cu)

        mMap.addMarker(
            MarkerOptions()
                .position(startLatLng) //                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        mMap.addMarker(
            MarkerOptions()
                .position(endLatLng) //                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        GoogleDirection.withServerKey("AIzaSyD0BCXGsMPd1V2hFI7vpJIho07UaUpM2LY")
            .from(startLatLng)
            .to(endLatLng)
            .avoid(AvoidType.FERRIES)
            .alternativeRoute(false)
            .transportMode(TransportMode.DRIVING)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?) {
                    Log.d("TAG", "onDirectionSuccess: " + direction?.routeList)
                    val directionPositionList: ArrayList<LatLng> =
                        direction?.routeList?.get(0)?.legList?.get(0)?.directionPoint!!
                    if (polyline == null) {
                        polyline = mMap.addPolyline(
                            DirectionConverter.createPolyline(
                                this@HomeRiderActivity,
                                directionPositionList,
                                3,
                                Color.BLACK
                            )
                        )
                    } else {
                        polyline!!.remove()
                        polyline = mMap.addPolyline(
                            DirectionConverter.createPolyline(
                                this@HomeRiderActivity,
                                directionPositionList,
                                3,
                                Color.BLACK
                            )
                        )
                    }
                    Log.d("TAG", "safsafsafsadgsaasf: ")

                    val routeArray = direction.routeList
                    for (i in 0 until routeArray.size) {
                        val legs1 = direction.routeList[i].legList[0]
                        val distance = legs1.distance
                        val duration = legs1.duration
                        totalDistance += distance.value
                        totalDuration += duration.value
                        totalDistance /= 1000
                        totalDuration /= 60
                        totalDistance = (totalDistance * 10) / 10.0
                        Log.d("TAG", "Total_distance:  - ${distance.text}")
                        Log.d("TAG", "Total_duration: - ${duration.text}")
                    }
                    // binding.timeDistanceTotalTv.text = String.format("Estimated Distance : %.2f km And Time : %.0f mins", totalDistance,totalDuration)
                    //getVehicleApi(totalDistance)
                }

                override fun onDirectionFailure(t: Throwable) {
                    Log.d("TAG", "onDirectionFailure: " + t.message.toString())
                }
            })
    }
    private fun getRiderOrderList(riderId: String) {
        val taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        taxiViewModel.riderOrderListApi(riderId).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        riderOrderListAdapter = RiderOrderListAdapter(
                            this@HomeRiderActivity,
                            it.data,
                            object : RiderOrderListAdapter.ClickListener {
                                override fun onClick(pos: Int) {
                                    startActivity(Intent(this@HomeRiderActivity,
                                        RideDetailActivity::class.java)
                                        .putExtra(AppConstant.trackOrderDetail, it.data[pos]))
                                }
                            })
                        binding.include.contentHome.riderRecycle.adapter = riderOrderListAdapter
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    //var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + vv, this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.include.contentHome.headerHome.homepageToolbarSwitchToUser -> {
                startActivity(
                    Intent(
                        this@HomeRiderActivity,
                        UserHomePageNavigationActivity::class.java
                    )
                )
                finish()
            }

            binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor -> {
                shopLoginChk()
            }

            binding.include.contentHome.starttrip -> {
                startActivity(Intent(this@HomeRiderActivity, AboutStartTripActivity::class.java))
            }

        }

    }

    private fun goLoginScreen() {
        Utils.showMessage(THIS!!, "You have to login first")
        startActivity(Intent(this@HomeRiderActivity, LoginActivity::class.java))
        finish()
    }

    private fun shopLoginChk() {
        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isVendor) {
                //  MyApp.popErrorMsg("","Your Shop details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this@HomeRiderActivity,
                        ShopOwnerHomePageNavigationActivity::class.java
                    )
                )
            } else {
                startActivity(Intent(this@HomeRiderActivity, ShopUserSignupActivityNew::class.java))
            }
        } else {
            goLoginScreen()
        }
    }

    private fun riderLoginChk() {
        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isRider) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(Intent(this@HomeRiderActivity, CurrentOrderListActivity::class.java))
            } else {
                startActivity(Intent(this@HomeRiderActivity, VehicleInfoActivity::class.java))
            }
        } else {
            goLoginScreen()
        }
    }


    private fun initView() {
        statusBarColor()

        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToUser)
        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor)
        //setTouchNClick(binding.include.contentHome.riderChkDemand)
        setTouchNClick(binding.include.contentHome.starttrip)

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
            /*PreferenceKeeper.instance.isUserLogin=false
            PreferenceKeeper.instance.loginResponse = null
*/
            PreferenceKeeper.instance.clearData()
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
        binding.includeLeftDrawer.homepageDrawerMyAccount.visibility = View.VISIBLE
        binding.includeLeftDrawer.view6.visibility = View.VISIBLE

        binding.include.constraintSendMoney.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(
                        this@HomeRiderActivity,
                        SendMoneyHomePageActivity::class.java
                    )
                )
            }
        }

        binding.include.constraintAddMoney.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(
                        this@HomeRiderActivity,
                        UserVerificationAddMoneyActivity::class.java
                    )
                )
            }

        }

        binding.include.constraintMakePayment.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(this@HomeRiderActivity, AddMoneyMainActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }

        }



        binding.include.constraintCallFriends.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(this@HomeRiderActivity, ContactListActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }

        }
        binding.include.constraintMessage.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(this@HomeRiderActivity, ChatListActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }

        }

/*
        binding.include.contentHome.riderDutyStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            checkDemandAPI(isChecked)
        }*/

    }

    private fun setSpinnerSideMenu() {
        val list = ArrayList<String>()
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
                            startActivity(
                                Intent(
                                    this@HomeRiderActivity,
                                    UserHomePageNavigationActivity::class.java
                                )
                            )
                            finish()
                            //   riderLoginChk()
                        }

                        2 -> {
                            shopLoginChk()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }
    private fun openCloseNavigationDrawerStart() {
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
                Utils.showMessage(this@HomeRiderActivity, resources.getString(R.string.press_again))
                back_pressed_time = System.currentTimeMillis()
            }
        }
    }

    private fun checkDemandAPI(isChecked: Boolean, riderId: String) {
        val taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        isRiderOnline = isChecked
        val json = JSONObject()
        json.put("driverId", "$riderId")
        json.put("taxiId", "$riderId")
        json.put("driverOnDuty", isRiderOnline)

        taxiViewModel.checkDemandRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        riderSwitchButton()
                        PreferenceKeeper.instance.isDriverOnline = isRiderOnline
                        startActivity(
                            Intent(
                                this@HomeRiderActivity,
                                CheckDemandActivity::class.java
                            ).putExtra("RiderStatus", isRiderOnline)
                        )
                    }
                }

                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }

                Status.ERROR -> {
                    binding.include.contentHome.riderDutyStatusSwitch.isChecked = false
                    binding.include.contentHome.riderDutyStatusSwitch.text =
                        getString(R.string.offline)
                    binding.include.contentHome.riderDutyStatusSwitch.setTextColor(
                        resources.getColor(
                            R.color.color_red
                        )
                    )

                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun getProfileAPI() {
        val taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        taxiViewModel.getRiderProfileApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.data?.let { data->
                        binding.include.contentHome.riderUserName.text =
                            "${data.firstName.capitalize() } ${data.lastName.capitalize()}"
                        data.totalEarning.let { earning->binding.include.contentHome.riderTotalAmount.text = "\$$earning" }
                        data.totalBookings.let { trip->binding.include.contentHome.riderTotalTrip.text = trip.toString() }
                        data.todayEarning.let { earning->binding.include.contentHome.riderAmountEarnToday.text = "\$$earning" }
                        data.todayBookings.let { trip->binding.include.contentHome.riderTotalTripToday.text = trip.toString() }
                        binding.include.contentHome.riderDutyStatusSwitch.setOnClickListener {
                            checkDemandAPI(binding.include.contentHome.riderDutyStatusSwitch.isChecked,data._id)
                        }
                        getRiderOrderList(data._id)
                        notificationModel?.let { noti->
                            riderRequestPopUp(data._id,noti)
                        }

                    }
                }

                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun acceptAPI(riderId: String,bookingNumber: String, latitude: String, longitude: String) {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "$bookingNumber")
        json.put("driverId", "$riderId")
        json.put("taxiId", "$riderId")
        json.put("driverLatitude", "$latitude")
        json.put("driverLongitude", "$longitude")
        json.put("driverAddress", "this is Address")

        taxiViewModel.acceptBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        dialog.isShowing.let {
                            dialog.dismiss()
                            notificationModel = null
                            getProfileAPI()

                        }
                    }
                }

                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun declineAPI(riderId: String,bookingNumber: String, latitude: String, longitude: String) {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "$bookingNumber")
        json.put("driverId", "$riderId")
        json.put("taxiId", "$riderId")
        json.put("driverLatitude", "$latitude")
        json.put("driverLongitude", "$longitude")
        json.put("driverAddress", "this is Address")

        taxiViewModel.declineBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        dialog.isShowing.let {
                            dialog.dismiss()
                        }
                    }
                }

                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun completeAPI(riderId: String,bookingNumber: String, latitude: String, longitude: String) {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "$bookingNumber")
        json.put("driverId", "$riderId")
        json.put("taxiId", "$riderId")
        json.put("driverLatitude", "$latitude")
        json.put("driverLongitude", "$longitude")
        json.put("driverAddress", "this is Address")

        taxiViewModel.completeBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        dialog.isShowing.let {
                            dialog.dismiss()
                            notificationModel = null
                            getProfileAPI()
                        }
                    }
                }

                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }


    private fun locationServiceForRiderDuty(){
        val serviceStarted = LocationService.isServiceStarted
        if (!serviceStarted) {

            if (isRiderOnline) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        Intent(
                            this,
                            LocationService::class.java
                        )
                    )
                } else {
                    startService(
                        Intent(
                            this,
                            LocationService::class.java
                        )
                    )
                }
                Log.d("TAG", "onCreate: Location_Service_Started:true")

            } else {
//                stopService(
//                    Intent(
//                        this,
//                        LocationService::class.java
//                    )
//                )
                Log.d("TAG", "onCreate: Location_Service_Started:Online_False")
            }
        } else {
            Log.d("TAG", "onCreate: Location_Service_Started:true1")
        }
    }

    private fun riderSwitchButton(){
        binding.include.contentHome.riderDutyStatusSwitch.isChecked = isRiderOnline
        if (isRiderOnline) {
            binding.include.contentHome.riderDutyStatusSwitch.text =
                getString(R.string.online)
            binding.include.contentHome.riderDutyStatusSwitch.setTextColor(
                resources.getColor(
                    R.color.color_006400
                )
            )
        } else {
            binding.include.contentHome.riderDutyStatusSwitch.text =
                getString(R.string.offline)
            binding.include.contentHome.riderDutyStatusSwitch.setTextColor(
                resources.getColor(
                    R.color.color_red
                )
            )
        }
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