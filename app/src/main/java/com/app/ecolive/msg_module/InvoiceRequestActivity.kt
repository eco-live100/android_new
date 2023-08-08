package com.app.ecolive.msg_module

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityInvoiceRequestBinding
import com.app.ecolive.payment_module.AddMoneyMainActivity
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils

class InvoiceRequestActivity : AppCompatActivity() {
    lateinit var binding :ActivityInvoiceRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_invoice_request)
        statusBarColor()
    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text="Invoice Request"
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.txtFriendlist.setOnClickListener {
            startActivity(
                Intent(this@InvoiceRequestActivity, FriendsListActivity::class.java)

            )
        }
        binding.sendinvoice.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        var countPeople ="0"
        val dialog = Dialog(this )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_pay_invoice)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        val pay = dialog.findViewById(R.id.pay) as AppCompatButton


        pay.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,PaymentMethod::class.java))
        }

        dialog.show()

    }

}