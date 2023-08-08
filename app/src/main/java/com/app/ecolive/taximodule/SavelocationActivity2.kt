package com.app.ecolive.taximodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivitySaveLocation2Binding
import com.app.ecolive.databinding.ActivitySaveLocationBinding
 import com.app.ecolive.utils.Utils
import java.util.*


class SavelocationActivity2 : AppCompatActivity() {
    lateinit var binding: ActivitySaveLocation2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_save_location2)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text = "Set destination on map"
        binding.toolbar.ivBack.setOnClickListener {
            finish()

        }
        binding.buttonDone.setOnClickListener {
            startActivity(Intent(this,VehicalListActivity::class.java))
        }

    }
}