package com.app.ecolive.rider_module

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.RidedetailActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity
import org.json.JSONObject

class RideDetailActivity  :  BaseActivity(){
    lateinit var binding : RidedetailActivityBinding

    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@RideDetailActivity,R.layout.ridedetail_activity)
        setToolBar()

        binding.pDetailAccept.setOnClickListener {
            acceptAPI()
        }
        binding.pDetailDecline.setOnClickListener {
            declineAPI()
        }
        binding.pDetailComplete.setOnClickListener {
            declineAPI()
        }
    }

    private fun setToolBar() {
        binding.toolbarRideDetail.ivBack.setOnClickListener { finish() }
        binding.toolbarRideDetail.toolbarTitle.text="Ride Detail"
        Utils.changeStatusColor(this, R.color.color_050D4C)
     //  Utils.changeStatusTextColor(this)
    }
    private fun acceptAPI() {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "BookTaxi64974")
        json.put("driverId","653ffc78c803328a408022aa")
        json.put("bookingNumber", "BookTaxi64974")
        json.put("driverId","653ffc78c803328a408022aa")
        json.put("taxiId","653ffc78c803328a408022aa")
        json.put("driverLatitude",343343)
        json.put("driverLongitude",34345)
        json.put("driverAddress","this is Address")

        taxiViewModel.acceptBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        finish()
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
    private fun declineAPI() {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "BookTaxi64974")
        json.put("driverId","653ffc78c803328a408022aa")
        json.put("bookingNumber", "BookTaxi64974")
        json.put("driverId","653ffc78c803328a408022aa")
        json.put("taxiId","653ffc78c803328a408022aa")
        json.put("driverLatitude",343343)
        json.put("driverLongitude",34345)
        json.put("driverAddress","this is Address")

        taxiViewModel.declineBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        finish()
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
    private fun completeAPI() {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "BookTaxi64974")
        json.put("driverId","653ffc78c803328a408022aa")
        json.put("bookingNumber", "BookTaxi64974")
        json.put("driverId","653ffc78c803328a408022aa")
        json.put("taxiId","653ffc78c803328a408022aa")
        json.put("driverLatitude",343343)
        json.put("driverLongitude",34345)
        json.put("driverAddress","this is Address")

        taxiViewModel.completeBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        finish()
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
}