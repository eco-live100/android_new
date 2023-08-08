package com.app.ecolive.user_module


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R

import com.app.ecolive.databinding.ActivityUserPaymentCheckoutBinding
import com.app.ecolive.payment_module.SucessActivity
import com.app.ecolive.utils.PopUpCommonMsg
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity


class UserPaymentCheckoutActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserPaymentCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@UserPaymentCheckoutActivity, R.layout.activity_user_payment_checkout)
        setToolBar()
        binding.btnPay.setOnClickListener {

            showPopUp()
        }
    }

    private fun showPopUp()   {
        PopUpCommonMsg.getInstance().createDialog(this@UserPaymentCheckoutActivity,"Transaction declined: Invalid card number",object:PopUpCommonMsg.Dialogclick{
            override fun onYes() {
                startActivity(Intent(this@UserPaymentCheckoutActivity,SucessActivity::class.java))
            }

            override fun onNo() {
                Log.d("TAG", "onNo: ")
            }

        })
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
         binding.toolbarUserPayment.toolbarTitle.text= "Payment"
        binding.toolbarUserPayment.ivBack.setOnClickListener {
            finish()
        }
    }
}