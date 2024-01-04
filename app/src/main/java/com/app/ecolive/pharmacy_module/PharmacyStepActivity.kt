package com.app.ecolive.pharmacy_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyStepBinding
import com.app.ecolive.pharmacy_module.health_profile.DoctorListPharmacyActivity
import com.app.ecolive.pharmacy_module.health_profile.PharmacyOrderFormActivity
import com.app.ecolive.pharmacy_module.health_profile.UserPrescriptionList
import com.app.ecolive.utils.Utils

class PharmacyStepActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyStepBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_step)
        binding.toolbar.toolbarTitle.text = getString(R.string.ordering_form)
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.requestPrescriptionNow.setOnClickListener {
          startActivity(Intent(this, DoctorListPharmacyActivity::class.java))
        }
        binding.placeOrder.setOnClickListener {
          startActivity(Intent(this, PharmacyOrderFormActivity::class.java))
        }
        binding.prescribeRecivedBtn.setOnClickListener {
          startActivity(Intent(this,UserPrescriptionList::class.java))
        }
    }
}