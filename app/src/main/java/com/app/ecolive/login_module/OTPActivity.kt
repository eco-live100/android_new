package com.app.ecolive.login_module

import `in`.aabhasjindal.otptextview.OTPListener
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.common_screen.UserTypeOptionActivity
import com.app.ecolive.databinding.OtpActivityBinding
import com.app.ecolive.msg_module.cometchat
import com.app.ecolive.service.Status
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel

import com.offercity.base.BaseActivity
import org.json.JSONObject


class OTPActivity : BaseActivity() {
    lateinit var binding: OtpActivityBinding
    private lateinit var countDownTimer: CountDownTimer
   // private var mLineProgressBar: CircleProgressBar? = null
    private val progressDialog = CustomProgressDialog()
    private var COUNTRY_CODE: String? =""
    private var FROM: String? =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(THIS!!, R.layout.otp_activity)
        setToolBar()
        initView()

        var mobNo: String? = intent.getStringExtra(AppConstant.MOBILE_NUMBER)
        COUNTRY_CODE = intent.getStringExtra(AppConstant.COUNTRY_CODE)
        FROM = intent.getStringExtra("FROM")

        if (mobNo!!.isNotEmpty()) {
            var mob1 = ""
            var mob2 = ""
            if (mobNo.length > 2) {
                mob2 = mobNo.substring(mobNo.length - 2)
                mob1 = mobNo.substring(0, 2)
            }
            binding.otpPlzVerify.setText(resources.getString(R.string.EnterOTPreceived) + " $COUNTRY_CODE" + mob1 + "******" + mob2)
        }
        if(!FROM.equals("forgetpassword")){
            sendMobilOtpAPICAll()

        }else{
            countdownTimer()
        }

    }

    private fun initView() {
        setTouchNClick(binding.otpSubmitBtn)
        setTouchNClick(binding.otpSendAgain)

     //   mLineProgressBar = binding!!.mLineProgressBar
        //  countdownTimer()
        binding.otpSubmitBtn.isEnabled = false
        binding.otpSubmitBtn.isClickable = false

        binding.containerOtp.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                if (binding.containerOtp.otp?.length!! < 6) {
                    binding.otpSubmitBtn.isEnabled = false
                    binding.otpSubmitBtn.isClickable = false
                }
            }

            override fun onOTPComplete(otp: String) {
                binding.otpSubmitBtn.isEnabled = true
                binding.otpSubmitBtn.isClickable = true
            }

        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (binding.otpSubmitBtn == v) {
            verifyMobileOtpAPICall()
        } else if (v == binding.otpSendAgain) {

            sendMobilOtpAPICAll()
        }
    }

    private fun sendMobilOtpAPICAll() {
        progressDialog.show(THIS!!)
        var otpViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()
        json.put("sendOtpType", "normal")//forgetpassword
        json.put("countryCode", "$COUNTRY_CODE")
        json.put("mobileNumber", intent.getStringExtra(AppConstant.MOBILE_NUMBER))

        otpViewModel.sendMobileOtp(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
                        countdownTimer()
                        Utils.showMessage(THIS!!, it.message)

                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, THIS!!)
                }
            }
        }
    }


    private fun verifyMobileOtpAPICall() {
        progressDialog.show(THIS!!)
        var otpViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()
        json.put("verifyOtpType", FROM)//forgetpassword
        json.put("otpCode", ""+binding.containerOtp.otp.toString())
        json.put("mobileNumber", intent.getStringExtra(AppConstant.MOBILE_NUMBER))

        otpViewModel.verifyMobileOtp(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        if(FROM.equals("forgetpassword")){
                            startActivity(Intent(THIS, LoginActivity::class.java))
                            Utils.showMessage(THIS!!, it.message)
                            finish()
                        }else{
                            var vv = it.data
                            PreferenceKeeper.instance.bearerTokenSave = it.data.accessToken
                            PreferenceKeeper.instance.isUserLogin = true
                            PreferenceKeeper.instance.loginResponse = it.data
                            // startActivity(Intent(THIS, UserHomePageNavigationActivity::class.java))
                            val uid = ""+PreferenceKeeper.instance.loginResponse?._id // Replace with the UID for the user to be created
                            val name = ""+PreferenceKeeper.instance.loginResponse?.firstName+" "+PreferenceKeeper.instance.loginResponse?.lastName // Replace with the name of the user
                            cometchat.register(uid,name)

                            startActivity(Intent(THIS, UserHomePageNavigationActivity::class.java))
                            Utils.showMessage(THIS!!, it.message)
                            finish()
                        }


                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, THIS!!)
                }
            }
        }
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(this)
    }

    private fun countdownTimer() {
        //  simulateProgress()
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli
                val elapsedSeconds: Long = diff / secondsInMilli
                if (elapsedSeconds < 10)
                    binding.tvTimer.text = "0$elapsedMinutes : 0$elapsedSeconds"
                else
                    binding.tvTimer.text = "0$elapsedMinutes : $elapsedSeconds"
                binding.otpSendAgain.isClickable = false
                binding.otpSendAgain.isEnabled = false
            }

            override fun onFinish() {
                binding.otpSendAgain.isClickable = true
                binding.otpSendAgain.isEnabled = true
            }
        }.start()
    }

/*    private fun simulateProgress() {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            mLineProgressBar!!.progress = progress
        }
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = 30000
        animator.start()
    }*/
}