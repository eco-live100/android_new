package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.SendmoneyInterntionlBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.MyApp.Companion.hideSoftKeyboard
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.toast
import com.app.ecolive.viewmodel.CommonViewModel
import com.app.ecolive.viewmodel.PaymentViewModel
import com.bumptech.glide.Glide
import org.json.JSONObject


class SendMoneyInternationLocl : AppCompatActivity() {

    lateinit var binding: SendmoneyInterntionlBinding
    var ID = ""
    var EMAIL = ""
    var userName = ""
    var paymentFor = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this@SendMoneyInternationLocl,
            R.layout.sendmoney_interntionl
        )
        ID = intent.getStringExtra("ID") ?: ""
        EMAIL = intent.getStringExtra("EMAIL") ?: ""
        binding.email.text = EMAIL
        getMyProfile()

        setToolBar()
        binding.noteEdt.addTextChangedListener {
            binding.txtCounter.text = (200 - binding.noteEdt.text.length).toString() + "/200"
        }
        binding.sendMoneyCountinueBtn.setOnClickListener {
            hideSoftKeyboard(this)
            if (!binding.sendAmount.text.isNullOrEmpty())
                if (AppConstant.walletAmount.toDouble() > binding.sendAmount.text.toString()
                        .toDouble()
                )
                    sendMoneyApi()
                else toast("Insufficient balance")
            else binding.sendAmount.error = "Enter amount"
        }
        paymentFor = binding.radioGoodsNService.text.toString()
        binding.radioGoodsNService.setOnClickListener {
            binding.radioCashAdvance.isChecked = false
            binding.radioGift.isChecked = false
            binding.radioOther.isChecked = false
            paymentFor = binding.radioGoodsNService.text.toString()
        }
        binding.radioGift.setOnClickListener {
            binding.radioCashAdvance.isChecked = false
            binding.radioGoodsNService.isChecked = false
            binding.radioOther.isChecked = false
            paymentFor = binding.radioGift.text.toString()

        }
        binding.radioCashAdvance.setOnClickListener {
            binding.radioGift.isChecked = false
            binding.radioGoodsNService.isChecked = false
            binding.radioOther.isChecked = false
            paymentFor = binding.radioCashAdvance.text.toString()

        }
        binding.radioOther.setOnClickListener {
            binding.radioGift.isChecked = false
            binding.radioGoodsNService.isChecked = false
            binding.radioCashAdvance.isChecked = false
            paymentFor = binding.radioOther.text.toString()

        }
        binding.addMoney.setOnClickListener { startActivity(Intent(this@SendMoneyInternationLocl,UserVerificationAddMoneyActivity::class.java)) }
    }

    private fun setToolBar() {
        binding.toolbarSendMoneyInter.toolbarTitle.text = "Send Money"
        binding.toolbarSendMoneyInter.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)

    }

    override fun onStart() {
        super.onStart()
        getWalletAmountApi()

    }

    private fun getWalletAmountApi() {
        //  binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()

        paymentViewModel.getWalletApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        AppConstant.walletAmount = it.data.money.toString()
                        AppConstant.walletCurrency = it.data.currency.toString()
                        "${it.data.money} ${it.data.currency}".also {
                            binding.sendInternationAmt.text = it
                        }

                    }
                    //   binding.isShimmerShow =false

                }

                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun sendMoneyApi() {
        if(binding.noteEdt.text.isNullOrEmpty()){
            binding.noteEdt.error ="Enter notes"
            return
        }

        //  binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()
        json.put("receiverId", ID)
        json.put("transactionType", 3)
        json.put("amount", binding.sendAmount.text.toString().trim())
        json.put("currency", "YEN")
        json.put("paymentFor", paymentFor)
        json.put("paymentNote", binding.noteEdt.text.toString().trim())

        paymentViewModel.sendMoneyApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        Toast.makeText(this, "" + it.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.sendAmount.setText("")
                        binding.noteEdt.setText("")
                        AppConstant.walletAmount = it.data.money.toString()
                        AppConstant.walletCurrency = it.data.currency.toString()
                        "${it.data.money} ${it.data.currency}".also {
                            binding.sendInternationAmt.text = it
                        }

                    }
                    //   binding.isShimmerShow =false

                }

                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun getMyProfile() {
        binding.isShimmerShow =true
        var myProfileViewModel = CommonViewModel(this)
        var userid = ID

        myProfileViewModel.getMyProfile(userid!!).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                       EMAIL =it.data.email?:""
                       userName = (it.data.firstName + " " + it.data.lastName) ?: ""
                        binding.email.text = EMAIL
                        binding.userName.text =userName
                        binding.isShimmerShow =false

                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    binding.isShimmerShow =true

                    var vv = it.message
                    MyApp.popErrorMsg("", "Can't make payment try again", this)
                }
            }
        }
    }


}