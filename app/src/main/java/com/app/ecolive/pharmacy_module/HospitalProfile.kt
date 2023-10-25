package com.app.ecolive.pharmacy_module

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityHospitalProfileBinding

class HospitalProfile : AppCompatActivity() {
    lateinit var binding : ActivityHospitalProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = window.decorView.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            window.decorView.systemUiVisibility = flags
        }
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_hospital_profile)
        binding.add.setOnClickListener {
            startActivity(Intent(this, HospitalUserActivity::class.java))
        }
        binding.toolbarTitle.text = getString(R.string.profile)
        binding.editProfileIcon.setOnClickListener {
            startActivity(Intent(this, CreateHospitalActivity::class.java))
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher
        }
    }
}