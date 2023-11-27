package com.app.ecolive.login_module


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityLoginBinding
import com.app.ecolive.msg_module.cometchat
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.offercity.base.BaseActivity
import org.json.JSONObject


class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    var userType = ""
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        getExtraDataIntent()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("fire_base_newToken", token)
            PreferenceKeeper.instance.fcmTokenSave= token

        })
        initView()
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        if (MyApp.isConnectingToInternet(THIS!!)) {
            if (v == binding.loginButton) {
                if (isValidInput()) {
                    loginApiCall()
                }
            } else if (v == binding.loginForgot) {
                startActivity(Intent(THIS!!, ForgotPwdActivity::class.java))
            }
        } else {
            MyApp.popErrorMsg("", resources.getString(R.string.No_Internet), THIS!!)
        }
    }



    private fun loginApiCall() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()
        json.put("emailMobile", binding.loginPhNo.text.toString())
        json.put("password", binding.loginPwd.text.toString())
        json.put("fcmToken", PreferenceKeeper.instance.fcmTokenSave)

        loginViewModel.userLogin(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
                        PreferenceKeeper.instance.bearerTokenSave = it.data.accessToken
                      //  if (binding.loginIsRemeberMe.isChecked) {
                            PreferenceKeeper.instance.isUserLogin = true
                       // }
                        PreferenceKeeper.instance.loginResponse = it.data
                        val uid = ""+PreferenceKeeper.instance.loginResponse?._id // Replace with the UID for the user to be created
                        val name = ""+PreferenceKeeper.instance.loginResponse?.firstName+" "+PreferenceKeeper.instance.loginResponse?.lastName // Replace with the name of the user
                        cometchat.register(uid,name)
                        startActivity(Intent(THIS, UserHomePageNavigationActivity::class.java))
                        finish()

                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                  var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                   // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun isValidInput(): Boolean {
        if (binding.loginPhNo.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter email", THIS!!)
            return false
        } else if (binding.loginPwd.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter password", THIS!!)
            return false
        }
        return true
    }

    private fun initView() {
        statusBarColor()
        setTouchNClick(binding.loginButton)
        setTouchNClick(binding.loginForgot)
        setTouchNClick(binding.loginFbBtn)
        setTouchNClick(binding.loginGoogleBtn)
        setTouchNClick(binding.loginTwitterBtn)

        binding.signupLabel.setOnClickListener {
            startActivity(Intent(this@LoginActivity, UserSignupActivity::class.java))
            finish()

        }


        binding.loginSkipNow.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    UserHomePageNavigationActivity::class.java
                )
            )

            finish()
        }

    }

    private fun statusBarColor() {
        Utils.changeStatusColor(THIS!!, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(THIS!!)
    }

    private fun getExtraDataIntent() {
        when {
            intent.getStringExtra(AppConstant.INTENT_EXTRAS.USER_TYPE) != null -> {
                userType = intent.getStringExtra(AppConstant.INTENT_EXTRAS.USER_TYPE)!!
            }
        }
    }




}