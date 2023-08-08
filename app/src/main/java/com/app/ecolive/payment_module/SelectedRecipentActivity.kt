package com.app.ecolive.payment_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPaymentMethodBinding
import com.app.ecolive.databinding.ActivitySelectedRecipentBinding
import com.app.ecolive.utils.Utils

class SelectedRecipentActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectedRecipentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_selected_recipent)
        binding.toolbar.toolbarTitle.text ="Selected recipients"
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.paybutton.setOnClickListener {
            startActivity(Intent(this,SucessActivity::class.java))
        }
        binding.Cancelbutton.setOnClickListener {
          finish()
        }
    }
}