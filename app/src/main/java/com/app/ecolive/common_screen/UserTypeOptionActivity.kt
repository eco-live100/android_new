package com.app.ecolive.common_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.R.color
import com.app.ecolive.utils.Utils.Companion.changeStatusColor
import com.app.ecolive.databinding.ActivityUserTypeOptionBinding
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.shop_owner.ShopUserSignupActivityNew
import com.app.ecolive.utils.AppConstant

class UserTypeOptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserTypeOptionBinding
    var userType = ""
    var intentType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_type_option)
        getExtraDataIntent()
        statusBarColor()
        initView()
    }

    private fun getExtraDataIntent() {
        when {
            intent.getStringExtra(AppConstant.INTENT_EXTRAS.INTENT_TYPE) != null -> {
                intentType = intent.getStringExtra(AppConstant.INTENT_EXTRAS.INTENT_TYPE)!!
            }
        }
    }

    private fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        binding.constraintUser.setOnClickListener {
            userType = "user"
            filterSelection(binding.radioButton)
        }
        binding.constraintRider.setOnClickListener {
            userType = "rider"
            filterSelection(binding.radioButton1)
        }
        binding.constraintShopOwner.setOnClickListener {
            userType = "owner"
            filterSelection(binding.radioButton2)
        }

        binding.btnNext.setOnClickListener {


            if (userType == "user") {
                startActivity(
                    Intent(
                        this@UserTypeOptionActivity,
                        UserHomePageNavigationActivity::class.java
                    )
                )


            } else if (userType == "rider") {
                startActivity(Intent(this@UserTypeOptionActivity, VehicleInfoActivity::class.java)
                    .putExtra(AppConstant.IS_FROM_USERTYPEOPTIONACTIVITY,true))

            } else if (userType == "owner") {
                startActivity(
                    Intent(
                        this@UserTypeOptionActivity,
                        ShopUserSignupActivityNew::class.java
                    )
                        .putExtra(AppConstant.IS_FROM_USERTYPEOPTIONACTIVITY,true)
                )
            }
            finish()
        }

    }

    private fun filterSelection(radioSelected: ImageView) {
        binding.radioButton.setImageResource(R.drawable.ic_ration_unselect)
        binding.radioButton1.setImageResource(R.drawable.ic_ration_unselect)
        binding.radioButton2.setImageResource(R.drawable.ic_ration_unselect)
        radioSelected.setImageResource(R.drawable.ic_radio_selected)
    }

    private fun statusBarColor() {
        changeStatusColor(this, color.color_050D4C)
        //  changeStatusTextColor(this)
    }

}