package com.app.ecolive.rider_module

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.TrackingStepAdapter
import com.app.ecolive.databinding.ActivityTrackingWithProgressBinding
import com.app.ecolive.taximodule.model.TaxiBookingRequestList
import com.app.ecolive.user_module.TrackingWithMapActivity
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.model.LatLng

class TrackingWithProgressActivity : AppCompatActivity() {
    lateinit var binding: ActivityTrackingWithProgressBinding
    private var trackOrderDetail : TaxiBookingRequestList.Data? = null
    private lateinit var startLatLng: LatLng
    private lateinit var endLatLng: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_with_progress)
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.toolbar.toolbarTitle.text = "Track order"
        statusBarColor()
        val items = listOf(
            TrackingStepAdapter.MyItem(false, "1 may\n9:30pm", "Documentation Prepared"),
            TrackingStepAdapter.MyItem(false, "1 may\n9:30pm", "Booking Arranged"),
            TrackingStepAdapter.MyItem(true, "1 may\n9:30pm", "In Transit"),
            TrackingStepAdapter.MyItem(false, "", "Arrived at Destination"),
            TrackingStepAdapter.MyItem(false, "", "Out of delivery"),
            TrackingStepAdapter.MyItem(false, "", "Delivered")
        )
        binding.sequenceLayout.setAdapter(TrackingStepAdapter(items))

        trackOrderDetail =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    AppConstant.trackOrderDetail,
                    TaxiBookingRequestList.Data::class.java
                )
            } else {
                intent.getSerializableExtra(AppConstant.trackOrderDetail) as TaxiBookingRequestList.Data
            }

        trackOrderDetail?.let {
            binding.apply {
                when (it.bookingStatus) {
                    "requested" -> {
                        //tvTotBill.visibility = View.GONE
                        bookingStatusTv.text="${it.bookingStatus}".capitalize()
                        bookingStatusTv.setTextColor(resources.getColor(R.color.color_red))
                    }
                    "accepted" -> {
                        //tvTotBill.visibility = View.VISIBLE
                        bookingStatusTv.text="${it.bookingStatus}".capitalize()
                        bookingStatusTv.setTextColor(resources.getColor(R.color.color_006400))
                    }
                }
                binding.appCompatLoginButton.setOnClickListener {_->
                    Intent(this@TrackingWithProgressActivity, TrackingWithMapActivity::class.java).also { intent->
                        intent.putExtra(AppConstant.trackOrderDetail, it)
                        startActivity(intent)
                    }
                }
                binding.orderNumberTv.text = "Order Number :- ${it.bookingNumber}"
                bookingDateTv.text="${it.createdAt}"
                fromAddressTv.text="${it.fromAddress}"
                toAddressTv.text="${it.toAddress}"
                distanceTv.text="${it.distanceInKm}Km"
                tvTotBill.text="\$${it.amount}"
            }
            startLatLng = LatLng(it.fromLatitude.toDouble(), it.fromLongitude.toDouble())
            endLatLng = LatLng(it.toLatitude.toDouble(), it.toLongitude.toDouble())
        }

    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}