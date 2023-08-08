package com.app.ecolive.rider_module

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.RidedetailActivityBinding
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity

class RideDetailActivity  :  BaseActivity(){
    lateinit var binding : RidedetailActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@RideDetailActivity,R.layout.ridedetail_activity)
        setToolBar()
    }

    private fun setToolBar() {
        binding.toolbarRideDetail.ivBack.setOnClickListener { finish() }
        binding.toolbarRideDetail.toolbarTitle.text="Ride Detail"
        Utils.changeStatusColor(this, R.color.color_050D4C)
     //  Utils.changeStatusTextColor(this)
    }
}