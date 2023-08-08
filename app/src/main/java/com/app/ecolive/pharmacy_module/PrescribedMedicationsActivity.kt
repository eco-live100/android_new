package com.app.ecolive.pharmacy_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescribedMedicationsBinding
import com.app.ecolive.utils.Utils

class PrescribedMedicationsActivity : AppCompatActivity() {
    lateinit var binding :ActivityPrescribedMedicationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_prescribed_medications)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Medical Prescription"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
    }
}