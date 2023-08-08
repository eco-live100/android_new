package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityHospitalUserBinding
import com.app.ecolive.databinding.ActivityPharmacyOptionBinding
import com.app.ecolive.user_module.ProductListActivity
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils

class PharmacyOptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_option)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Pharmacy"
        binding.toolbar.help.visibility = View.GONE
        binding.AllHospitalAndPharmacy.setOnClickListener {  }

        binding.hospitalPharmacyProfile .setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CreateHospitalActivity::class.java
                )
            )
        }
        binding.HealthProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PharmacyProcessActivity::class.java
                )
            )
        }
        binding.AllMedication.setOnClickListener {
            startActivity(
                Intent(
                    this@PharmacyOptionActivity,
                    ProductListActivity::class.java
                )
                    .putExtra(AppConstant.CATEGORY, AppConstant.PHARMACY)
            )
        }



    }
}