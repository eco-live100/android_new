package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ScanqrcodeActivityBinding
import com.app.ecolive.databinding.SendmoneytobankActivityBinding
import com.app.ecolive.utils.Utils

class ScanQrActivity:AppCompatActivity() {
    lateinit var binding: ScanqrcodeActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@ScanQrActivity,R.layout.scanqrcode_activity)
        statusBarColor()
        setToolBar()
        binding.qrCodePrcced.setOnClickListener {
            startActivity(Intent(this@ScanQrActivity,SucessActivity::class.java))
        }
    }

    private fun setToolBar() {
        binding.toolbarQRCode.toolbarTitle.text="Scan QR"
        binding.toolbarQRCode.ivBack.setOnClickListener { finish() }
    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}