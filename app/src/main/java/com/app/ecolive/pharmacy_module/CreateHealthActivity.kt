package com.app.ecolive.pharmacy_module

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCreateHealthBinding
import com.app.ecolive.databinding.ActivityPharmacyProcessBinding
import com.app.ecolive.payment_module.RecipentListListActivity
import com.app.ecolive.payment_module.SendMoneyHomePageActivity
import com.app.ecolive.utils.Utils

class CreateHealthActivity : AppCompatActivity() {
    lateinit var binding:ActivityCreateHealthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_create_health)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Eco- Live Health Profile"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.nextButton.setOnClickListener {
            if(binding.userName.text.toString().trim()!=""){
                if (binding.address.text.toString().trim() != ""){
                    if (binding.ssnNumber.text.toString().trim() != ""){
                        startActivity(Intent(this,PharmacyInsuranceActivity::class.java)
                            .putExtra("name",binding.userName.text.toString().trim())
                            .putExtra("address",binding.address.text.toString().trim())
                            .putExtra("ssn",binding.ssnNumber.text.toString().trim())
                        )
                    }else{
                        Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please enter SSN number", Toast.LENGTH_SHORT).show()
            }
          
        }
    }


}