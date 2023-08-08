package com.app.ecolive.rider_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityRiderSignupBinding
import com.app.ecolive.databinding.ActivityUserTypeOptionBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.utils.Utils

class RiderSignupActivity : AppCompatActivity() {
    lateinit var binding: ActivityRiderSignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rider_signup)
        statusBarColor()
        initView()
    }
    private fun initView() {

        binding.appCompatButton.setOnClickListener {
            startActivity(Intent(this@RiderSignupActivity, VehicleInfoActivity::class.java))
        }

        binding.signInLabel.setOnClickListener { startActivity(Intent(this@RiderSignupActivity, LoginActivity::class.java)) }

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(this)
    }
}