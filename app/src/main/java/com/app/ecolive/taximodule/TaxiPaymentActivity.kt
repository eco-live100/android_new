package com.app.ecolive.taximodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTaxiPaymentBinding
import com.app.ecolive.utils.Utils

class TaxiPaymentActivity : AppCompatActivity() {
    lateinit var binding :ActivityTaxiPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_taxi_payment)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Payment options"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.paymentBussiness.setOnClickListener {
           startActivity(Intent(this,FriendListTaxiActivity::class.java))
        }

    }
}