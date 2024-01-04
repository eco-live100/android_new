package com.app.ecolive.pharmacy_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyOptionBinding
import com.app.ecolive.pharmacy_module.doctor.CreateAndUpdateDoctorProfile
import com.app.ecolive.pharmacy_module.doctor.DoctorProfile
import com.app.ecolive.pharmacy_module.health_profile.PharmacyProcessActivity
import com.app.ecolive.pharmacy_module.pharmacy.CreateAndUpdatePharmacyProfile
import com.app.ecolive.pharmacy_module.pharmacy.PharmacyProfile
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils

class PharmacyOptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_option)
        binding.toolbar.toolbarTitle.text ="Pharmacy"
        binding.toolbar.help.visibility = View.GONE

        binding.toolbar.ivBack.setOnClickListener {onBackPressed()}
        binding.hospitalPharmacyProfile .setOnClickListener {

            if (PreferenceKeeper.instance.loginResponse!!.isDoctor) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this,
                        DoctorProfile::class.java
                    )
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        CreateAndUpdateDoctorProfile::class.java
                    )
                )
            }
        }
        binding.HealthProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PharmacyProcessActivity::class.java
                )
            )
        }
        binding.pharmacyCardView.setOnClickListener {
            if (PreferenceKeeper.instance.loginResponse!!.isPharmacy) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this,
                        PharmacyProfile::class.java
                    )
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        CreateAndUpdatePharmacyProfile::class.java
                    )
                )
            }

        }
/*        binding.AllMedication.setOnClickListener {
            startActivity(
                Intent(
                    this@PharmacyOptionActivity,
                    ProductListActivity::class.java
                )
                    .putExtra(AppConstant.CATEGORY, AppConstant.PHARMACY)
            )
        }*/



    }
}