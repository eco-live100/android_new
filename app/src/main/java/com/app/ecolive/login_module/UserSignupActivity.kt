package com.app.ecolive.login_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityUserSignupBinding
import com.app.ecolive.databinding.ActivityUserTypeOptionBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.offercity.base.BaseActivity
import org.json.JSONObject

class UserSignupActivity : BaseActivity() {
    lateinit var binding: ActivityUserSignupBinding
    private val progressDialog = CustomProgressDialog()
    //google
    private val RC_SIGN_IN = 1
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_signup)
        statusBarColor()
        initView()
        binding.userName.setText(intent.getStringExtra(AppConstant.SOCIAL_LOGIN_NAME))
        binding.userEmail.setText(intent.getStringExtra(AppConstant.SOCIAL_LOGIN_EMAIL))
        //gooogle
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initView() {
        setTouchNClick(binding.userSignInBtn)
        setTouchNClick(binding.userSignupBtn)
        setTouchNClick(binding.signupGoogleBtn)

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.userSignInBtn -> {
                startActivity(Intent(this@UserSignupActivity, LoginActivity::class.java))
            }
            binding.userSignupBtn -> {
                if (isValidateInput()) {
                    userSignUpApiCall()
                }
            }
            binding.signupGoogleBtn -> {
                googleLogin()
            }
        }
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
//            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
//            handleSignInResult(result!!)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("ok", "firebaseAuthWithGoogle:" + account.id)
                Log.d("ok", "firebaseAuthWithGoogle:" + account.idToken!!)
                var vv1 = account.id
                var vv3 = account.email
                var vv4 = account.displayName
                var vv5 = account.photoUrl
                binding.userName.setText(account.displayName)
                binding.userEmail.setText(account.email)
                // firebaseAuthWithGoogle(account.idToken!!)
              //  socialLoginAPICall("google", account.id,account.email,account.displayName)
                googleSignInClient.signOut()//logout from google
                FirebaseAuth.getInstance().signOut()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("ok", "Google sign in failed", e)
            }
        }
    }

    private fun isValidateInput(): Boolean {
        if (binding.userName.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter your first name", THIS!!)
            return false

        } else if (binding.userNameLast.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter your last name", THIS!!)
            return false

        } else if (binding.userEmail.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter your Email", THIS!!)
            return false

        } else if (!MyApp.isValidEmail(binding.userEmail.text.toString())) {
            MyApp.popErrorMsg("", "Email is not valid ", THIS!!)
            return false

        } else if (binding.userPhno.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter mobile number", THIS!!)
            return false

        } else if (binding.userPhno.text.toString().length < 9) {
            MyApp.popErrorMsg("", "Please enter correct phone number", THIS!!)
            return false

        } else if (binding.userPwd.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter password", THIS!!)
            return false

        } else if (binding.userPwd.text.toString().length < 6) {
            MyApp.popErrorMsg("", resources.getString(R.string.pwd_shud_six), THIS!!)
            return false
        } else if (binding.userPwdCnfrm.text.toString().isBlank()) {
            MyApp.popErrorMsg("", "Please enter confirm password", THIS!!)
            return false

        } else if (binding.userPwd.text.toString() != binding.userPwdCnfrm.text.toString()) {
            MyApp.popErrorMsg("", "Password doesn't match with confirm password", THIS!!)
            return false

        }
        return true
    }

    private fun userSignUpApiCall() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()
        json.put("firstName", binding.userName.text.toString())
        json.put("lastName", binding.userNameLast.text.toString())
        json.put("email", binding.userEmail.text.toString())
        json.put("countryCode", binding.forgotContryPicker.selectedCountryCode.toString())
        json.put("mobileNumber", binding.userPhno.text.toString())
        json.put("password", binding.userPwd.text.toString())
        json.put("socialLoginType", intent.getStringExtra(AppConstant.SOCIAL_LOGIN_TYPE))
        json.put("socialLoginId", intent.getStringExtra(AppConstant.SOCIAL_LOGIN_ID))
        json.put("deviceType", "android")
        json.put("deviceId", "djksfhsdkhfdi")
        json.put("fcmToken", "dsgsdfgdfgfdg")
        json.put("latitude", "")
        json.put("longitude", "")

        loginViewModel.userSignUp(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
//                        PreferenceKeeper.instance.bearerTokenSave = it.data.auth_token
//                        PreferenceKeeper.instance.isUserLogin = true
//                        PreferenceKeeper.instance.loginResponse = it.data
                        startActivity(
                            Intent(THIS, OTPActivity::class.java)
                                .putExtra(
                                    AppConstant.MOBILE_NUMBER,
                                    binding.userPhno.text.toString()
                                ).putExtra(
                                    AppConstant.COUNTRY_CODE,
                                    binding.forgotContryPicker.selectedCountryCode.toString()
                                ).putExtra("FROM","normal")
                        )
                        finish()


                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message

//                    var jsonObject = JSONObject(it.message)
//                   MyApp.popErrorMsg("", "" + jsonObject.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(this)

        binding.signupBakBtn.setOnClickListener {
            startActivity(Intent(THIS!!, LoginActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(THIS!!, LoginActivity::class.java))
        super.onBackPressed()
    }

}