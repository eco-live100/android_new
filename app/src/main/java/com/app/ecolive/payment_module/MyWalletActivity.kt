package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.WalletActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.PaymentViewModel
import com.bumptech.glide.Glide
import org.json.JSONObject

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

        binding.addMoney.setOnClickListener {
            startActivity(Intent(this@MyWalletActivity,UserVerificationAddMoneyActivity::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
        binding.walletAmt.text ="${PreferenceKeeper.instance.loginResponse!!.wallet.money} ${PreferenceKeeper.instance.loginResponse!!.wallet.currency}"
        binding.walletUserName.text ="${PreferenceKeeper.instance.loginResponse?.firstName} ${PreferenceKeeper.instance.loginResponse?.lastName}"
        Glide.with(this).load(PreferenceKeeper.instance.loginResponse?.profilePicture).placeholder(R.drawable.ic_user_default).into(binding.walletProfile)
        getWalletAmountApi()
    }

    private fun getWalletAmountApi() {
        //  binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()

        paymentViewModel.getWalletApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        AppConstant.walletAmount = it.data.money.toString()
                        AppConstant.walletCurrency = it.data.currency.toString()
                        PreferenceKeeper.instance.loginResponse!!.wallet.money =it.data.money
                        binding.walletAmt.text ="${PreferenceKeeper.instance.loginResponse!!.wallet.money} ${PreferenceKeeper.instance.loginResponse!!.wallet.currency}"

                    }
                    //   binding.isShimmerShow =false

                }

                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

}