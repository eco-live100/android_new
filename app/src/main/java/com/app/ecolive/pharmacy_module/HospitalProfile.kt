package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityHospitalProfileBinding

class HospitalProfile : AppCompatActivity() {
    lateinit var binding :ActivityHospitalProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_hospital_profile)
        binding.add.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HospitalUserActivity::class.java
                )
            )
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}