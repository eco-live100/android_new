package com.app.ecolive.pharmacy_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyOrderFormBinding
import com.app.ecolive.databinding.ActivityPharmacyStepBinding
import com.app.ecolive.utils.Utils

class PharmacyOrderFormActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyOrderFormBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_order_form)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text =""
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

    }
}