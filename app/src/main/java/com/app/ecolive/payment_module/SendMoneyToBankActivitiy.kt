package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.SendmoneytobankActivityBinding
import com.app.ecolive.utils.Utils

class SendMoneyToBankActivitiy : AppCompatActivity() {
    lateinit var binding: SendmoneytobankActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SendMoneyToBankActivitiy,R.layout.sendmoneytobank_activity)
        statusBarColor()
        setToolBar()
        binding.bankAccount.setEndIconOnClickListener{
            startActivity(Intent(this@SendMoneyToBankActivitiy,ScanQrActivity::class.java))
        }
        binding.sendMoneyCountinueBtn.setOnClickListener{
            startActivity(Intent(this@SendMoneyToBankActivitiy,SucessActivity::class.java))
        }
    }

    private fun setToolBar() {
        binding.toolbarSendMoneyToBnk.toolbarTitle.text= "Send Money"
        binding.toolbarSendMoneyToBnk.ivBack.setOnClickListener { finish() }

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}