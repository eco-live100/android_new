package com.app.ecolive.msg_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPaymentMethodBinding
import com.app.ecolive.payment_module.SucessActivity
import com.app.ecolive.utils.Utils

class PaymentMethod : AppCompatActivity() {
    lateinit var binding :ActivityPaymentMethodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_payment_method)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Payment method"
        binding.paybutton.setOnClickListener {
            startActivity(Intent(this,SucessActivity::class.java))
        }
        binding.toolbar.ivBack.setOnClickListener {
           finish()
        }
    }
}