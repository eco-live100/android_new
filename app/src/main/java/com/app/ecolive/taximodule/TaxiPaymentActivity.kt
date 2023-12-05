package com.app.ecolive.taximodule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTaxiPaymentBinding
import com.app.ecolive.utils.Utils

class TaxiPaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityTaxiPaymentBinding

    companion object {
        var paymentType = "cash"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor2(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_taxi_payment)

        binding.toolbar.toolbarTitle.text = "Payment options"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        /*  binding.paymentBussiness.setOnClickListener {
             startActivity(Intent(this,FriendListTaxiActivity::class.java))
          }*/

        if (paymentType == "cash") {
            binding.cashCheckBox.visibility = View.VISIBLE
            binding.walletCheckBox.visibility = View.GONE
            paymentType = "cash"
        } else {
            binding.cashCheckBox.visibility = View.GONE
            binding.walletCheckBox.visibility = View.VISIBLE
            paymentType = "wallet"
        }


        binding.cashCardView.setOnClickListener {
            binding.cashCheckBox.visibility = View.VISIBLE
            binding.walletCheckBox.visibility = View.GONE
            paymentType = "cash"
        }

        binding.walletCardView.setOnClickListener {
            binding.cashCheckBox.visibility = View.GONE
            binding.walletCheckBox.visibility = View.VISIBLE
            paymentType = "wallet"
        }

    }
}