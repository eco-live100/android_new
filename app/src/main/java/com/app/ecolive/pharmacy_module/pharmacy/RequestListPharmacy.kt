package com.app.ecolive.pharmacy_module.pharmacy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.RequestListPharmacyBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.RequestListPharmacyAdapter
import com.app.ecolive.pharmacy_module.model.PrescriptionDataModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import org.json.JSONObject

class RequestListPharmacy : AppCompatActivity() {
    lateinit var binding: RequestListPharmacyBinding
    private lateinit var requestListDoctorAdapter: RequestListPharmacyAdapter
    private var requestList: ArrayList<PrescriptionDataModel> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    var pharmacyId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.request_list_pharmacy)
        binding.toolbar.toolbarTitle.text = getString(R.string.request_list)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        intent.extras?.let {
            pharmacyId = it.getString(AppConstant.pharmacyId).toString()
            getPrescriptionRequestForDoctorApi(pharmacyId)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recycleFriends.layoutManager = layoutManager
        requestListDoctorAdapter = RequestListPharmacyAdapter(this, requestList)
        binding.recycleFriends.adapter = requestListDoctorAdapter

        //getDoctorListApi()

    /*    binding.searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchedKey = binding.searchUserEditText.text.toString()
                doctorListAdapter.filter.filter(searchedKey)
            }
            override fun afterTextChanged(s: Editable) {}
        })*/
    }

    private fun getPrescriptionRequestForDoctorApi(doctorId: String) {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)
        val jsonObject = JSONObject()
        jsonObject.put("doctorId",doctorId)

        requestList.clear()
        viewModel.getPrescriptionRequestForDoctorApi(jsonObject).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        requestList.addAll(it.data)
                        requestListDoctorAdapter.notifyDataSetChanged()
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    val vv = it.message
                    //var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }
}