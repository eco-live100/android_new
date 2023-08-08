package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityHospitalProfileBinding
import com.app.ecolive.databinding.ActivityHospitalUserBinding

class HospitalUserActivity : AppCompatActivity() {

    lateinit var binding : ActivityHospitalUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_hospital_user)
        binding.order.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DoctorRegisterActivity::class.java
                )
            )
        }
    }
}