package com.app.ecolive.pharmacy_module.health_profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyListBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.PharmacyListAdapter
import com.app.ecolive.pharmacy_module.model.PharmacyData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils

class PharmacyListActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyListBinding
    lateinit var pharmacyListAdapter: PharmacyListAdapter
    private var pharmacyList: ArrayList<PharmacyData> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pharmacy_list)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text = getString(R.string.doctor_list_eco_live)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recycleFriends.layoutManager = layoutManager
        pharmacyListAdapter = PharmacyListAdapter(this, pharmacyList)
        binding.recycleFriends.adapter = pharmacyListAdapter

        getPharmacyListApi()

        binding.searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchedKey = binding.searchUserEditText.text.toString()
                pharmacyListAdapter.filter.filter(searchedKey)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getPharmacyListApi() {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)

        var lat = 26.8430479
        var long = 75.8145549
        var distance = 3000
        var page = 1
        var limit = 100

        pharmacyList.clear()
        viewModel.getPharmacyListApi(lat = lat, long = long, distance = distance, page = page, limit = limit).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        pharmacyList.addAll(it.data.items)
                        pharmacyListAdapter.notifyDataSetChanged()
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    //var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + vv, this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
}