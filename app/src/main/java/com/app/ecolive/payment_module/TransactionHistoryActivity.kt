package com.app.ecolive.payment_module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTransactionHistoryBinding
 import com.app.ecolive.payment_module.adapters.TransactionHistoryListAdapter
import com.app.ecolive.service.Status
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.PaymentViewModel
import org.json.JSONObject

class TransactionHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityTransactionHistoryBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_history)
        statusBarColor()
        initView()
        transactionListData()

    }

    private fun initView() {
        binding.include5.ivBack.setOnClickListener { finish() }
        binding.include5.toolbarTitle.text="Transaction history"
    }


    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun transactionListData() {


        getTransactionHistory()
    }

    private fun getTransactionHistory() {
        binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()
        json.put("page",1)
        json.put("limit",100)

        paymentViewModel.getTransactionHistory(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        binding.rvPaymentTransaction.layoutManager =
                            LinearLayoutManager(this)
                        val adapter = TransactionHistoryListAdapter(this, it.data.docs)
                        binding.rvPaymentTransaction.adapter = adapter

                    }
                    binding.isShimmerShow =false

                }
                Status.LOADING -> {}
                Status.ERROR -> {


                    var vv =  it.message
//                    var msg = JSONObject(it.message)
//                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                     MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }


}