package com.app.ecolive.login_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityResetPasswordBinding
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

class ResetPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    private val progressDialog = CustomProgressDialog()
    var userID :String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        userID =intent.getStringExtra("userId")?:""
        statusBarColor()
        initView()


    }

    private fun initView() {

        setTouchNClick(binding.userSignupBtn)

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {

            binding.userSignupBtn -> {
                if (isValidateInput()) {
                    ResetPasswordApiCall()
                }
            }

        }
    }



    private fun isValidateInput(): Boolean {
        if (binding.userPwd.text.toString().isBlank()) {
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

    private fun ResetPasswordApiCall() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()

        json.put("userId", userID)
        json.put("newPassword", binding.userPwd.text.toString())


        loginViewModel.resetPassword(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
//
                        startActivity(Intent(THIS, LoginActivity::class.java) )
                        Utils.showMessage(THIS!!, it.message)
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