package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.SendmoneyInterntionlBinding
import com.app.ecolive.utils.Utils

class SendMoneyInternationLocl:AppCompatActivity(),View.OnClickListener {

    lateinit var binding:SendmoneyInterntionlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = DataBindingUtil.setContentView(this@SendMoneyInternationLocl,R.layout.sendmoney_interntionl)
        statusBarColor()
        binding.sendMnyInternationText.setOnClickListener(this)
        binding.sendMnyLocalText.setOnClickListener(this)
        binding.sendMoneyCountinueBtn.setOnClickListener(this)

        setToolBar()
    }

    private fun setToolBar() {
        binding.toolbarSendMoneyInter.toolbarTitle.text= "Send Money"
        binding.toolbarSendMoneyInter.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)

    }

    override fun onClick(v: View?) {
         when(v){
             binding.sendMnyInternationText->{
                 binding.sendMnyLocalText.background = null
                 binding.sendMnyLocalText.setTextColor(resources.getColor(R.color.color_9F9F9F))

                 binding.sendMnyInternationText.background = resources.getDrawable(R.drawable.bg_gray_left_selected)
                 binding.sendMnyInternationText.setTextColor(resources.getColor(R.color.color_333333))

                 binding.sendMoneyUsdPrice.visibility= VISIBLE
                 binding.flagIndiaImg.setImageResource(R.drawable.ic_india_flag)
             }

             binding.sendMnyLocalText->{
                 binding.sendMnyInternationText.background = null
                 binding.sendMnyInternationText.setTextColor(resources.getColor(R.color.color_9F9F9F))

                 binding.sendMnyLocalText.background = resources.getDrawable(R.drawable.bg_gray_right_selected)
                 binding.sendMnyLocalText.setTextColor(resources.getColor(R.color.color_333333))

                 binding.sendMoneyUsdPrice.visibility=GONE
                 binding.flagIndiaImg.setImageResource(R.drawable.ic_uk_flag)

             }

             binding.sendMoneyCountinueBtn->{
//                 startActivity(Intent(this@SendMoneyInternationLocl,SendMoneyToBankActivitiy::class.java))
                 startActivity(Intent(this@SendMoneyInternationLocl,UserVerificationAddMoneyActivity::class.java))
             }
         }
    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}