package com.app.ecolive.pharmacy_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityHospitalProfileBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import org.json.JSONObject

class HospitalProfile : AppCompatActivity() {
    lateinit var binding : ActivityHospitalProfileBinding
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
       /* window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = window.decorView.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            window.decorView.systemUiVisibility = flags
        }*/
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_hospital_profile)
        binding.add.setOnClickListener {
            startActivity(Intent(this, HospitalUserActivity::class.java))
        }
        binding.toolbarTitle.text = getString(R.string.profile)
        binding.editProfileIcon.setOnClickListener {
            startActivity(Intent(this, CreatePharmacyProfileActivity::class.java)
                .putExtra("hospitalEmployeeUserId","")
            )
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher
        }
        //getProfile()
    }

    private fun getProfile() {
        progressDialog.show(this)
        var pharmacyViewModel = PharmacyViewModel(this)
        val userId = PreferenceKeeper.instance.loginResponse!!._id
        val professionType = "doctor"

        pharmacyViewModel.getProfile(userId = userId, professionType = professionType).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        Log.d("TAG", "getProfile: ${it.toString()}")
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + vv, this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
}