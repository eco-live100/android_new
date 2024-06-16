package com.app.ecolive.user_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityMyAccountBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.payment_module.MyWalletActivity
import com.app.ecolive.rider_module.HomeRiderActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PopUpVehicleChoose
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.Utils.Companion.progressDialog
import com.app.ecolive.viewmodel.CommonViewModel
import com.bumptech.glide.Glide
import org.json.JSONObject

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
        binding.toolbar.ivCart.visibility= View.GONE
         binding.constraintMyLocation.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyAddressActivity::class.java)) }
        binding.constraintMyOrder.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyOrderActivity2::class.java)) }
        binding.ivEditPencil.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, ProfileUpdateActivity::class.java)) }
         binding.MyWalletConstrent.setOnClickListener {  startActivity(Intent(this@MyAccountActivity, MyWalletActivity::class.java)) }


    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
    //    Utils.changeStatusTextColor(this)
    }

    override fun onStart() {
        super.onStart()
        getMyProfile()
    }

    private fun getMyProfile() {

        var myProfileViewModel = CommonViewModel(this)
        var userid =PreferenceKeeper.instance.loginResponse?._id

        myProfileViewModel.getMyProfile(userid!!).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        PreferenceKeeper.instance.loginResponse = it.data
                        binding.userName.text ="${PreferenceKeeper.instance.loginResponse?.firstName} ${PreferenceKeeper.instance.loginResponse?.lastName}"
                        binding.userMobile.text ="${PreferenceKeeper.instance.loginResponse?.countryCode} ${PreferenceKeeper.instance.loginResponse?.mobileNumber}"
                        binding.userGmail.text ="${PreferenceKeeper.instance.loginResponse?.email}"
                        Glide.with(this).load(PreferenceKeeper.instance.loginResponse?.profilePicture).placeholder(R.drawable.ic_user_default).into(binding.CurrentOrderProfileImage)

                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, this)
                }
            }
        }
    }


 }