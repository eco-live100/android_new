package com.app.ecolive.payment_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAddMoneyMainBinding
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils

class AddMoneyMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddMoneyMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_money_main)
        statusBarColor()

        /* binding.appCompatContinueButton.setOnClickListener { startActivity(Intent(this@UserVerificationAddMoneyActivity, AddMoneyMainActivity::class.java))
              }*/

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.include4.toolbarTitle.text="Add Money"
        binding.include4.ivBack.setOnClickListener { finish() }
    }

}