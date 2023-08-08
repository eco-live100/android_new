package com.app.ecolive.payment_module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.SucessActivityBinding

class SucessActivity : AppCompatActivity() {
    lateinit var binding:SucessActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@SucessActivity,R.layout.sucess_activity)
        binding.sucessBackBtn.setOnClickListener {
            finish()
        }
    }
}