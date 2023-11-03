package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyStepBinding
import com.app.ecolive.utils.Utils

class PharmacyStepActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyStepBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_step)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Ordering form"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.requestPrescriptionNow.setOnClickListener {
          startActivity(Intent(this,DoctorListPharmacyActivity::class.java))
        }
        binding.placeOrder.setOnClickListener {
          startActivity(Intent(this,PharmacyOrderFormActivity::class.java))
        }
        binding.prescribeRecivedBtn.setOnClickListener {
          startActivity(Intent(this,PrescribedMedicationsActivity::class.java))
        }
    }
}