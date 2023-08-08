package com.app.ecolive.user_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityNotifactionBinding
import com.app.ecolive.utils.Utils

class NotifactionActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotifactionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifaction)
        binding.toolbar.toolbarTitle.text = "Notification"
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        //    Utils.changeStatusTextColor(this)
    }
}