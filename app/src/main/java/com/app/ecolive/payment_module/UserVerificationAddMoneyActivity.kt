package com.app.ecolive.payment_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityUserVerificationAddMoneyBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.toast
import com.app.ecolive.viewmodel.PaymentViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import org.json.JSONObject

class UserVerificationAddMoneyActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserVerificationAddMoneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_user_verification_add_money)
        statusBarColor()
        binding.isShimmerShow =false

        binding.buttonContinue.setOnClickListener {
            addMoneyApi()
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        binding.toolbarAddMoney.toolbarTitle.text = "Add Money"
        binding.toolbarAddMoney.ivBack.setOnClickListener { finish() }
    }

    private fun addMoneyApi() {
        if (binding.amountEdittext.text.toString().trim()==""){
            toast("Enter amount")
            return
        }
          binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()
        json.put("amount",binding.amountEdittext.text.toString().trim())
        json.put("currency","YEN")

        paymentViewModel.addMoneyApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        startActivity(Intent(this@UserVerificationAddMoneyActivity,WebViewActivity::class.java).putExtra("URL",it.data.paymentUrl?:""))
                        finish()
                    }
                       binding.isShimmerShow =false

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    binding.isShimmerShow =false
                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }


}