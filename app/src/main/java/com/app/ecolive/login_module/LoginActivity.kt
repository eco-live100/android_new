package com.app.ecolive.login_module


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityLoginBinding
import com.app.ecolive.msg_module.cometchat
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel


import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.offercity.base.BaseActivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    var userType = ""
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        getExtraDataIntent()
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