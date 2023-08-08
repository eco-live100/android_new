package com.app.ecolive.payment_module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.WalletActivityBinding
import com.app.ecolive.utils.Utils

class MyWalletActivity : AppCompatActivity() {
    lateinit var binding:WalletActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MyWalletActivity,R.layout.wallet_activity)
        setToolBar()
    }

    private fun setToolBar() {
        binding.toolbarWallet.toolbarTitle.text= "Wallet"
        binding.toolbarWallet.ivBack.setOnClickListener { finish() }

        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}