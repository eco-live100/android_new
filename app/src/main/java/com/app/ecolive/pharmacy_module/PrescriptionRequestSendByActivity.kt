package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescriptionRequestBinding
import com.app.ecolive.databinding.ActivityPrescriptionRequestSendByBinding
import com.app.ecolive.utils.Utils

class PrescriptionRequestSendByActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrescriptionRequestSendByBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_prescription_request_send_by)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Prescription request sent by"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.prescribeBtn.setOnClickListener {
           startActivity(Intent(this,StartPrescribingActivity::class.java))
        }
    }
}
