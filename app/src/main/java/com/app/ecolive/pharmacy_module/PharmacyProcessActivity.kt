package com.app.ecolive.pharmacy_module

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyProcessBinding
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils

class PharmacyProcessActivity : AppCompatActivity() {
    lateinit var binding :ActivityPharmacyProcessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_process)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text =""
        binding.toolbar.help.visibility = View.GONE
        binding.skip.setOnClickListener {
            showDialogHelthAlert()
        }
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }


        binding.nextButton.setOnClickListener {
            if(PreferenceKeeper.instance.isHealthProfileCreate){
                startActivity(
                    Intent(
                        this,
                        MedicationListActivity::class.java
                    )
                )
            }else{
                startActivity(
                    Intent(
                        this,
                        CreateHealthActivity::class.java
                    )
                )
            }

        }
    }

    private fun showDialogHelthAlert() {
        val dialog = Dialog(this )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_skip_pharmicy)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        val goBack = dialog.findViewById(R.id.goBack) as TextView
        val ok = dialog.findViewById(R.id.ok) as AppCompatButton

        ok.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this,
                    CreateHealthActivity::class.java
                )
            )

        }

        goBack.setOnClickListener {
            dialog.dismiss()


        }

        dialog.show()

    }
}