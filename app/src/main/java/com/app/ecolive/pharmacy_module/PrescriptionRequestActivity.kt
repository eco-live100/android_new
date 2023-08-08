package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescriptionRequestBinding
import com.app.ecolive.utils.Utils

class PrescriptionRequestActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrescriptionRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_prescription_request)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Prescription request form to"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.Submit.setOnClickListener {
            startActivity(Intent(this,PrescriptionRequestSendByActivity::class.java))
        }
    }
}