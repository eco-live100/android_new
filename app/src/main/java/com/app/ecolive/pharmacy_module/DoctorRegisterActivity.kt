package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityDoctorRegisterBinding
import com.app.ecolive.databinding.ActivityHospitalUserBinding

class DoctorRegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityDoctorRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this,R.layout.activity_doctor_register)
        binding.finish.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AppoinmentActivity::class.java
                )
            )
        }
    }
}