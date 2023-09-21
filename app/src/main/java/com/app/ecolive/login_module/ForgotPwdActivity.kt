package com.app.ecolive.login_module

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ForgotActivityBinding
import com.app.ecolive.databinding.OtpActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.*
import com.app.ecolive.utils.Utils.Companion.progressDialog
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity
import org.json.JSONObject


class ForgotPwdActivity : BaseActivity() {
    lateinit var binding: ForgotActivityBinding
    lateinit var baseViewModel: CommonViewModel
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(THIS!!, R.layout.forgot_activity)
        //   binding.forgotContryPicker.registerPhoneNumberTextView(binding.forgotPhoneNumber)
        setToolBar()
        binding.submitButton.setOnClickListener {
            if (binding.forgotPhoneNumber.text.toString().isNotBlank()) {
                sendMobilOtpAPICAll()
            } else {
                MyApp.popErrorMsg("", "Enter the mobile number", THIS!!)
            }
        }
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(this)

        binding.forgotBakBtn.setOnClickListener { finish() }

    }

    private fun sendMobilOtpAPICAll() {
        progressDialog.show(THIS!!)
        val otpViewModel = CommonViewModel(THIS!!)
        val json = JSONObject()
        json.put("sendOtpType", "forgetpassword")//forgetpassword
        json.put("countryCode", binding.forgotContryPicker.selectedCountryCodeWithPlus.toString())
//        json.put("countryCode", "91")
        json.put("mobileNumber", binding.forgotPhoneNumber.text.toString())

        otpViewModel.sendMobileOtp(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    startActivity(
                        Intent(
                            this,
                            OTPActivity::class.java
                        ).putExtra(
                            AppConstant.MOBILE_NUMBER,
                            binding.forgotPhoneNumber.text.toString()
                        ).putExtra(
                            AppConstant.COUNTRY_CODE,
                            binding.forgotContryPicker.selectedCountryCodeWithPlus.toString()
                        ).putExtra("FROM","forgetpassword")
                    )
                }

                Status.LOADING -> {

                }
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    var msg =JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                }
            }
        }
    }

}