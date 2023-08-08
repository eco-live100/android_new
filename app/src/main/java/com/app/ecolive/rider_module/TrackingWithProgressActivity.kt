package com.app.ecolive.rider_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.TrackingStepAdapter
import com.app.ecolive.databinding.ActivityTrackingWithProgressBinding
import com.app.ecolive.databinding.ActivityUserHomePageNavigationBinding
import com.app.ecolive.user_module.TrackingWithMapActivity
import com.app.ecolive.utils.Utils

class TrackingWithProgressActivity : AppCompatActivity() {
    lateinit var binding: ActivityTrackingWithProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_with_progress)
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.toolbar.toolbarTitle.text="Track order"
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

        binding.appCompatLoginButton.setOnClickListener {   startActivity(Intent(this@TrackingWithProgressActivity, TrackingWithMapActivity::class.java))
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}