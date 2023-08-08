package com.app.ecolive.rider_module

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ChkdemandActivityBinding
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity

class CheckDemandActivity: BaseActivity() {
    lateinit var binding : ChkdemandActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(THIS!!,R.layout.chkdemand_activity)
        setToolBar()
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        binding.demandBackBtn.setOnClickListener {
            finish()
        }
    }
}