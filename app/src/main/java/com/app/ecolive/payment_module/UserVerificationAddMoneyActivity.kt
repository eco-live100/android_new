package com.app.ecolive.payment_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.GetStartedActivity
import com.app.ecolive.databinding.ActivityAddMoneyMainBinding
import com.app.ecolive.databinding.ActivityUserVerificationAddMoneyBinding
import com.app.ecolive.utils.Utils

class UserVerificationAddMoneyActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserVerificationAddMoneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_verification_add_money)
        statusBarColor()
        binding.appCompatContinueButton.setOnClickListener { startActivity(Intent(this@UserVerificationAddMoneyActivity, AddMoneyMainActivity::class.java))
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbarAddMoney.toolbarTitle.text="Add Money"
        binding.toolbarAddMoney.ivBack.setOnClickListener { finish() }
    }

}