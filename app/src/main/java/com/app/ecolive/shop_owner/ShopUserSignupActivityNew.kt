package com.app.ecolive.shop_owner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ShopusersignupActivityBinding
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity

class ShopUserSignupActivityNew : BaseActivity() {
    lateinit var binding: ShopusersignupActivityBinding
    var storeType="Sole Proprietors"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(THIS!!,R.layout.shopusersignup_activity)
        setToolBar()
        inItView()
    }

    private fun inItView() {
         setTouchNClick(binding.shopEcoLive)
         setTouchNClick(binding.shopStartSelling)
         setTouchNClick(binding.shopClickHere)
         setTouchNClick(binding.shopNextBtn)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if(v==binding.shopEcoLive){
            binding.shopStartSellingDesc.visibility=GONE
            binding.shopEcoLiveDesc.visibility=VISIBLE
            binding.shopTitle.setText(resources.getString(R.string.For_Sole_Proprietors))
            binding.shopLogo.setImageResource(R.drawable.ic_solo_logo)
            storeType="Sole Proprietors"

        }

        else if(v==binding.shopStartSelling){
                binding.shopStartSellingDesc.visibility=VISIBLE
                binding.shopEcoLiveDesc.visibility=GONE
            binding.shopTitle.setText(resources.getString(R.string.For_Corporation))
            binding.shopLogo.setImageResource(R.drawable.ic_corporation)
            storeType="Corporation"
        }
        else if(v==binding.shopNextBtn || v==binding.shopClickHere ){
            startActivity(Intent(THIS!!,ShopSignupSoleProprietors::class.java)
                .putExtra(AppConstant.STORE_TYPE,storeType))
        }
    }

    private fun setToolBar() {
      //  Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor2(this)
        binding.toolbarShop.toolbarTitle.text = "Shop User Sign up"
        binding.toolbarShop.ivBack.setOnClickListener { finish() }
    }


}