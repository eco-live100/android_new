package com.app.ecolive.payment_module

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ScanqrcodeActivityBinding
import com.app.ecolive.databinding.SendmoneytobankActivityBinding
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

class ScanQrActivity:AppCompatActivity() {
    lateinit var binding: ScanqrcodeActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@ScanQrActivity,R.layout.scanqrcode_activity)
        statusBarColor()
        setToolBar()
        binding.qrCodePrcced.setOnClickListener {
//            startActivity(Intent(this@ScanQrActivity,SucessActivity::class.java))
            finish()
        }
    }

    private fun setToolBar() {
        binding.toolbarQRCode.toolbarTitle.text="MY QR code"
        binding.toolbarQRCode.ivBack.setOnClickListener { finish() }
       binding.Myqrcode.setImageBitmap(getQrCodeBitmap())
    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    fun getQrCodeBitmap(): Bitmap {
        val size = 1024 //pixels
        val qrCodeContent = PreferenceKeeper.instance.loginResponse?._id.toString()?:""
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}