package com.app.ecolive.taximodule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivitySaveLocationBinding
 import com.app.ecolive.utils.Utils
import java.util.*


class SavelocationActivity : AppCompatActivity() {
    lateinit var binding: ActivitySaveLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_save_location)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text = "Add address below"
        binding.toolbar.ivBack.setOnClickListener {
            finish()

        }
    }
}