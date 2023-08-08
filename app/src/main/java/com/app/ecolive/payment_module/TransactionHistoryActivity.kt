package com.app.ecolive.payment_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.HomeCategoryListAdapter
import com.app.ecolive.databinding.ActivityTransactionHistoryBinding
import com.app.ecolive.databinding.ActivityUserTypeOptionBinding
import com.app.ecolive.localmodel.HomeCategoryListModel
import com.app.ecolive.localmodel.TransactionHistoryListModel
import com.app.ecolive.payment_module.adapters.TransactionHistoryListAdapter
import com.app.ecolive.user_module.ProductListActivity
import com.app.ecolive.utils.Utils

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
        val dataList = ArrayList<TransactionHistoryListModel>()
        var item = TransactionHistoryListModel("Angie O. Plasty","- $2,750.00","12 mar, 6:39 pm","minus",resources.getDrawable(R.drawable.dummy_female_user))
        dataList.add(item)
        item = TransactionHistoryListModel("Ella Vator","- $1,300.00","12 mar, 6:39 pm","minus",resources.getDrawable(R.drawable.dummy_male_user))
        dataList.add(item)
         item = TransactionHistoryListModel("Manuel Labor","- $4,600.00","12 mar, 6:39 pm","minus",resources.getDrawable(R.drawable.dummy_female_user))
        dataList.add(item)
         item = TransactionHistoryListModel("Ash Wednesday","+ $1,100.00","12 mar, 6:39 pm","plus",resources.getDrawable(R.drawable.dummy_female_user))
        dataList.add(item)
         item = TransactionHistoryListModel("Sharon Needles","- $2,300.00","12 mar, 6:39 pm","minus",resources.getDrawable(R.drawable.dummy_male_user))
        dataList.add(item)
         item = TransactionHistoryListModel("Gene Jacket","- $2,200.00","12 mar, 6:39 pm","minus",resources.getDrawable(R.drawable.dummy_female_user))
        dataList.add(item)

        binding.rvPaymentTransaction.layoutManager =
            LinearLayoutManager(this)
       val adapter = TransactionHistoryListAdapter(this, dataList)
        binding.rvPaymentTransaction.adapter = adapter

    }

}