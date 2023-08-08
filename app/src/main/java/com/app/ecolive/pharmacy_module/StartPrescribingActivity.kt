package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescriptionRequestSendByBinding
import com.app.ecolive.databinding.ActivityStartPrescribingBinding
import com.app.ecolive.utils.Utils

class StartPrescribingActivity : AppCompatActivity() {
    lateinit var binding: ActivityStartPrescribingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_start_prescribing)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Start prescribing now"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

    }
}