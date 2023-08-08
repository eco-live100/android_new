package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAceptedDeclineOrderBinding
import com.app.ecolive.databinding.ActivityMyAccountBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.payment_module.MyWalletActivity
import com.app.ecolive.rider_module.HomeRiderrActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.shop_owner.ShopUserSignupActivityNew
import com.app.ecolive.utils.PopUpVehicleChoose
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide

class MyAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_account)
        statusBarColor()
        initView()
    }
    private fun initView() {
        binding.toolbar.toolbarTitle.text="My Account"
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.userName.text ="${PreferenceKeeper.instance.loginResponse?.firstName} ${PreferenceKeeper.instance.loginResponse?.lastName}"
        binding.userMobile.text ="${PreferenceKeeper.instance.loginResponse?.countryCode} ${PreferenceKeeper.instance.loginResponse?.mobileNumber}"
        binding.userGmail.text ="${PreferenceKeeper.instance.loginResponse?.email}"

        Glide.with(this).load(PreferenceKeeper.instance.loginResponse?.profilePicture).placeholder(R.drawable.ic_user_default).into(binding.CurrentOrderProfileImage)
        binding.toolbar.ivCart.visibility= View.VISIBLE
        binding.toolbar.ivCart.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyCartActivity::class.java)) }
        binding.constraintMyLocation.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyAddressActivity::class.java)) }
        binding.constraintMyOrder.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyOrderActivity2::class.java)) }
        binding.ivEditPencil.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, ProfileUpdateActivity::class.java)) }
        binding.CurrentOrderProfileImage.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, FullImageActivity::class.java)) }
        binding.MyWalletConstrent.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyWalletActivity::class.java)) }
        binding.constraintDelivery.setOnClickListener{
            showPOPUP()
        }
        binding.constraintNotification.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, NotifactionActivity::class.java)) }

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
    //    Utils.changeStatusTextColor(this)
    }

    private fun showPOPUP() {
        PopUpVehicleChoose.getInstance().createDialog(
            this@MyAccountActivity,
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
        Utils.showMessage(this, resources.getString(R.string.you_login_first))
        startActivity(Intent(this@MyAccountActivity, LoginActivity::class.java))
        finish()
    }


    private fun riderLoginChk() {
        // startActivity(Intent(this@UserHomePageNavigationActivity, HomeRiderrActivity::class.java))

        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isRider) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this@MyAccountActivity,
                        HomeRiderrActivity::class.java
                    )
                )
                finish()
            } else {
                startActivity(
                    Intent(
                        this@MyAccountActivity,
                        VehicleInfoActivity::class.java
                    )
                )
            }
        } else {
            goLoginScreen()
        }
    }
}