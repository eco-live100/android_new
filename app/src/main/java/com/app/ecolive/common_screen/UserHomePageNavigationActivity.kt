package com.app.ecolive.common_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.BuildCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.HomeCategoryListAdapter
import com.app.ecolive.common_screen.adapters.HomeProductListAdapter
import com.app.ecolive.common_screen.adapters.HomeProductListAdapter2
import com.app.ecolive.databinding.ActivityUserHomePageNavigationBinding
import com.app.ecolive.login_module.LocationPickerActivity
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.msg_module.ZegoCallChatActivity
import com.app.ecolive.payment_module.SendMoneyHomePageActivity
import com.app.ecolive.payment_module.SendMoneyInternationLocl
import com.app.ecolive.pharmacy_module.PharmacyOptionActivity
import com.app.ecolive.rider_module.HomeRiderActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.shop_owner.ShopUserSignupActivityNew
import com.app.ecolive.taximodule.TaxiHomeActivity
import com.app.ecolive.user_module.*
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.offercity.base.BaseActivity
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.config.ZegoHangUpConfirmDialogInfo
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler
import com.zegocloud.zimkit.services.ZIMKit
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import im.zego.zim.entity.ZIMError
import im.zego.zim.enums.ZIMErrorCode
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


class UserHomePageNavigationActivity : BaseActivity() {

    lateinit var binding: ActivityUserHomePageNavigationBinding
    var adapter: HomeCategoryListAdapter? = null
    var adapterProduct: HomeProductListAdapter? = null
    var adapterProduct2: HomeProductListAdapter2? = null
    private var drawerLayout: DrawerLayout? = null
    private val progressDialog = CustomProgressDialog()
    val scanCustomCode = registerForActivityResult(ScanCustomCode(), ::handleResult)
    private var back_pressed_time: Long = 0
    private val PERIOD: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_home_page_navigation)
        initView()
        binding.includeLeftDrawer.sideMenuUserName.text =
            "Hello, ${PreferenceKeeper.instance.loginResponse?.firstName}"

        var usedId = "" + PreferenceKeeper.instance.loginResponse?._id
        var userName = PreferenceKeeper.instance.loginResponse?.firstName ?: ""
        val avatarUrl =
            if (PreferenceKeeper.instance.loginResponse?.profilePicture.isNullOrEmpty()) {
                "https://cdn-icons-png.flaticon.com/256/149/149071.png"
            } else {
                PreferenceKeeper.instance.loginResponse?.profilePicture.toString()
            }
        binding.include.contentHome.headerHome.addresstitle.text = MyApp.lastLocationAddresstitle
        binding.include.contentHome.headerHome.address.text = MyApp.lastLocationAddress
        binding.include.contentHome.headerHome.addresstitle.setOnClickListener {
            val intentaddress =
                Intent(this@UserHomePageNavigationActivity, LocationPickerActivity::class.java)
            intentaddress.putExtra("Key", "ForSelect")
            resultActivity.launch(intentaddress)
        }
        binding.include.contentHome.headerHome.address.setOnClickListener {
            val intentaddress =
                Intent(this@UserHomePageNavigationActivity, LocationPickerActivity::class.java)
            intentaddress.putExtra("Key", "ForSelect")
            resultActivity.launch(intentaddress)
        }
        ZIMKit.connectUser(usedId, userName, avatarUrl) { error: ZIMError ->
            if (error.code != ZIMErrorCode.SUCCESS) {
                val message = error.message + ": " + error.code.value()
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                return@connectUser
            }

        }
        initCallInviteService(KeyCenter.APP_ID2, KeyCenter.APP_SIGN2, usedId, userName)


        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                 // Back is pressed... Finishing the activity

            }
        })

        /*if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
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
        } else {*/
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
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
            })
       // }



    }


    override fun onClick(v: View?) {
        super.onClick(v)
        if (v == binding.include.contentHome.headerHome.homepageToolbarSwitchToRider) {

            riderLoginChk()

        } else if (v == binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor) {
            shopLoginChk()
        }

    }


    private fun goLoginScreen() {
        Utils.showMessage(THIS!!, resources.getString(R.string.you_login_first))
        startActivity(Intent(this@UserHomePageNavigationActivity, LoginActivity::class.java))
        finish()
    }

    private fun shopLoginChk() {

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

    }

    private fun riderLoginChk() {

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
        shopCategoriesListAPICAll()
        vendorShopProductListAPICAll()
        vendorShopProductListAPICAll2()

        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToRider)
        setTouchNClick(binding.include.contentHome.headerHome.homepageToolbarSwitchToVendor)

        drawerLayout = binding.drawerLayout

        binding.include.contentHome.headerHome.ivMenu.setOnClickListener {
            try {
                Utils.hideSoftKeyBoard(this@UserHomePageNavigationActivity)
            } catch (e: Exception) {
            }
            openCloseNavigationDrawerStart()
        }

        binding.include.contentHome.tvSelectCategory.setOnClickListener { openCloseNavigationDrawerEnd() }



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

            val builder = MaterialAlertDialogBuilder(this, R.style.Theme_MyApp_Dialog_Alert)
            // val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Alert !").setMessage("Are you sure want to logout ?")
                .setPositiveButton("Logout") { dialog, which -> logoutApi() }

            builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            val alert = builder.create()
            alert.show()

        }



        binding.includeLeftDrawer.homepageDrawerMyOrder.setOnClickListener {
            startActivity(
                Intent(
                    this@UserHomePageNavigationActivity,
                    MyOrderActivity2::class.java
                )
            )
        }

        binding.includeLeftDrawer.MyCart.setOnClickListener {
            startActivity(
                Intent(
                    this@UserHomePageNavigationActivity,
                    MyCartActivity::class.java
                )
            )
        }

        if (PreferenceKeeper.instance.loginResponse == null) {
            binding.includeLeftDrawer.homepageDrawerMyAccount.visibility = View.GONE
            binding.includeLeftDrawer.homepageDrawerMyOrder.visibility = View.GONE
        } else {
            binding.includeLeftDrawer.homepageDrawerMyAccount.visibility = View.VISIBLE
            binding.includeLeftDrawer.homepageDrawerMyOrder.visibility = View.VISIBLE
        }



        binding.includeLeftDrawer.view6.visibility = View.VISIBLE

        /*  binding.include.constraintSendMoney.setOnClickListener {
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
          }*/

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
                    Intent(
                        this@UserHomePageNavigationActivity,
                        SendMoneyHomePageActivity::class.java
                    )

                )
            }

        }

        binding.include.constraintCallFriends.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(this@UserHomePageNavigationActivity, ContactListActivity::class.java)

                )
            }

        }

        binding.include.constraintMessage.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse == null) {
                goLoginScreen()
            } else {
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        ZegoCallChatActivity::class.java
                    )

                )
            }

        }


        binding.include.contentHome.showmoreProduct.setOnClickListener {
            startActivity(
                Intent(this@UserHomePageNavigationActivity, ProductListActivity::class.java)
                    .putExtra(AppConstant.CATEGORY, "Live")
            )
        }

        binding.include.contentHome.showmoreProduct2.setOnClickListener {
            startActivity(
                Intent(this@UserHomePageNavigationActivity, ProductListActivity::class.java)
                    .putExtra(AppConstant.CATEGORY, "Online")
            )
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
        when (result) {
            is QRResult.QRSuccess -> {
                var id = result.content.rawValue
                if (PreferenceKeeper.instance.loginResponse?._id.equals(id)) {
                    Toast.makeText(
                        this@UserHomePageNavigationActivity,
                        "Can't make payment your self",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                startActivity(
                    Intent(
                        this@UserHomePageNavigationActivity,
                        SendMoneyInternationLocl::class.java
                    ).putExtra("ID", id)
                )
            }

            QRResult.QRUserCanceled -> "User canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
            else -> {}
        }


    }

    private fun logoutApi() {
        progressDialog.show(this)
        var myProfileViewModel = CommonViewModel(this)

        myProfileViewModel.logoutApi("").observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()

                    it.data?.let {
                        PreferenceKeeper.instance.isUserLogin = false
                        PreferenceKeeper.instance.loginResponse = null
                        PreferenceKeeper.instance.isHealthProfileCreate = false
                        PreferenceKeeper.instance.isDriverOnline = false
                        PreferenceKeeper.instance.lastLocationLang = ""
                        PreferenceKeeper.instance.lastAddress = ""
                        PreferenceKeeper.instance.lastLocationLat = ""
                        PreferenceKeeper.instance.lastAddressTitle = ""
                        val i = Intent(applicationContext, LoginActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("EXIT", true)
                        startActivity(i)
                        ZIMKit.disconnectUser()
                        ZegoUIKitPrebuiltCallInvitationService.unInit()
                        finish()

                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()

                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, this)
                }
            }
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
                                        Toast.makeText(
                                            this@UserHomePageNavigationActivity,
                                            "We are coming soon ",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else if (pos == 2) {
                                        Toast.makeText(
                                            this@UserHomePageNavigationActivity,
                                            "We are coming soon ",
                                            Toast.LENGTH_SHORT
                                        ).show()


                                    } else if (pos == 3) {
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

                                    } else {
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


    private fun vendorShopProductListAPICAll() {
        // progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = HashMap<String, String>()
        json.put("distance", "3")
        json.put("latitude", MyApp.locationLast!!.latitude.toString())
        json.put("longitude", MyApp.locationLast!!.longitude.toString())

        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.vendorShopProductList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                    //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        binding.include.contentHome.productRecyclerview.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        adapterProduct = HomeProductListAdapter(
                            this,
                            it.data.docs,
                            object : HomeProductListAdapter.ClickListener {
                                override fun viewProductDetails(productId: String) {
                                    startActivity(
                                        Intent(
                                            this@UserHomePageNavigationActivity,
                                            ProductDetailActivity::class.java
                                        ).putExtra("productId", productId)
                                    )
                                }
                            })
                        binding.include.contentHome.productRecyclerview.adapter = adapterProduct
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    //  progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun vendorShopProductListAPICAll2() {
        // progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = HashMap<String, String>()



        Log.d("ok", "addProductAPICall: " + json)
        addProductViewModel.vendorShopProductList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall: ")
                    //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        binding.include.contentHome.productRecyclerview2.layoutManager =
                            GridLayoutManager(this, 2)
                        adapterProduct2 = HomeProductListAdapter2(
                            this,
                            it.data.docs,
                            object : HomeProductListAdapter2.ClickListener {
                                override fun viewProductDetails(productId: String) {
                                    startActivity(
                                        Intent(
                                            this@UserHomePageNavigationActivity,
                                            ProductDetailActivity::class.java
                                        ).putExtra("productId", productId)
                                    )
                                }
                            })
                        binding.include.contentHome.productRecyclerview2.adapter = adapterProduct2
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    //  progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }


    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        // Utils.changeStatusTextColor(this)
    }





    private fun shopRegister() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_shop_register)
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );

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


    fun initCallInviteService(
        appID: Long,
        appSign: String?,
        userID: String?,
        userName: String?
    ) {
        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
        callInvitationConfig.provider =
            ZegoUIKitPrebuiltCallConfigProvider { invitationData -> getConfig(invitationData) }
        ZegoUIKitPrebuiltCallService.events.errorEventsListener =
            ErrorEventsListener { errorCode, message -> Timber.d("onError() called with: errorCode = [$errorCode], message = [$message]") }
        ZegoUIKitPrebuiltCallService.events.invitationEvents.pluginConnectListener =
            SignalPluginConnectListener { state, event, extendedData ->
                Timber.d(
                    "onSignalPluginConnectionStateChanged() called with: state = [" + state + "], event = [" + event
                            + "], extendedData = [" + extendedData + "]"
                )
            }
        ZegoUIKitPrebuiltCallService.init(
            application, appID, appSign, userID, userName,
            callInvitationConfig
        )
        ZegoUIKitPrebuiltCallService.events.callEvents.callEndListener =
            CallEndListener { callEndReason, jsonObject ->
                Timber.d(
                    "onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                            + "]"
                )
            }
        ZegoUIKitPrebuiltCallService.events.callEvents.setExpressEngineEventHandler(
            object : IExpressEngineEventHandler() {
                override fun onRoomStateChanged(
                    roomID: String, reason: ZegoRoomStateChangedReason, errorCode: Int,
                    extendedData: JSONObject
                ) {
                    Timber.d(
                        "onRoomStateChanged() called with: roomID = [" + roomID + "], reason = [" + reason
                                + "], errorCode = [" + errorCode + "], extendedData = [" + extendedData + "]"
                    )
                }
            })
        ZegoUIKitPrebuiltCallService.events.setBackPressEvent {
            ZegoUIKitPrebuiltCallService.minimizeCall()
            true
        }
    }

    fun getConfig(invitationData: ZegoCallInvitationData): ZegoUIKitPrebuiltCallConfig? {
        val isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.value
        val isGroupCall = invitationData.invitees.size > 1
        val callConfig: ZegoUIKitPrebuiltCallConfig
        callConfig = if (isVideoCall && isGroupCall) {
            ZegoUIKitPrebuiltCallConfig.groupVideoCall()
        } else if (!isVideoCall && isGroupCall) {
            ZegoUIKitPrebuiltCallConfig.groupVoiceCall()
        } else if (!isVideoCall) {
            ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall()
        } else {
            ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
        }
        callConfig.hangUpConfirmDialogInfo = ZegoHangUpConfirmDialogInfo()
        return callConfig
    }

    val resultActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
            if (results.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = results.data
                if (data!!.getBooleanExtra("result", false)) {
                    binding.include.contentHome.headerHome.addresstitle.text =
                        MyApp.lastLocationAddresstitle
                    binding.include.contentHome.headerHome.address.text = MyApp.lastLocationAddress
                }
            }

        }

}