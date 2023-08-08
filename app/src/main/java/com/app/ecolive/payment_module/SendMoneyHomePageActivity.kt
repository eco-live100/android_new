package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivitySendMoneyHomePageBinding
import com.app.ecolive.utils.Utils

class SendMoneyHomePageActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySendMoneyHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SendMoneyHomePageActivity,R.layout.activity_send_money_home_page)
        statusBarColor()
        setToolBar()
        binding.sendMoneyAddMoneyBtn.setOnClickListener(this)
        binding.sendMoneyScanQr.setOnClickListener(this)
        binding.sendMoneyBnkTrans.setOnClickListener(this)
        binding.tvBalanceHistory.setOnClickListener(this)
    }

    private fun setToolBar() {

        binding.toolbarSendMoney.ivBack.setOnClickListener { finish() }
        binding.toolbarSendMoney.toolbarTitle.text="Send to Eco-live"
        binding.toolbarSendMoney.cutmToolBarRightIcon.visibility=VISIBLE

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }



    override fun onClick(v: View?) {
         when(v){
             binding.sendMoneyAddMoneyBtn->{
                 startActivity(Intent(this@SendMoneyHomePageActivity,UserVerificationAddMoneyActivity::class.java))
             }

             binding.sendMoneyScanQr->{
                 startActivity(Intent(this@SendMoneyHomePageActivity,ScanQrActivity::class.java))
             }

             binding.sendMoneyBnkTrans->{
                 startActivity(Intent(this@SendMoneyHomePageActivity,SendMoneyInternationLocl::class.java))
             }
             binding.tvBalanceHistory->{
                 startActivity(Intent(this@SendMoneyHomePageActivity,TransactionHistoryActivity::class.java))
             }

         }
    }
}