package com.app.ecolive.pharmacy_module.health_profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.UserPrescriptionListBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.UserPrescriptionListAdapter
import com.app.ecolive.pharmacy_module.model.UserPrescriptionData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils

class UserPrescriptionList : AppCompatActivity() {
    lateinit var binding: UserPrescriptionListBinding
    private lateinit var userPrescriptionListAdapter: UserPrescriptionListAdapter
    private var userPrescriptionList: ArrayList<UserPrescriptionData> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.user_prescription_list)
        binding.toolbar.toolbarTitle.text = getString(R.string.medical_prescription)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recycleView.layoutManager = layoutManager
        userPrescriptionListAdapter = UserPrescriptionListAdapter(this, userPrescriptionList)
        binding.recycleView.adapter = userPrescriptionListAdapter

        getDoctorListApi()

    /*    binding.searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchedKey = binding.searchUserEditText.text.toString()
                doctorListAdapter.filter.filter(searchedKey)
            }
            override fun afterTextChanged(s: Editable) {}
        })*/
    }

    private fun getDoctorListApi() {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)

        userPrescriptionList.clear()
        viewModel.userPrescriptionListApi().observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        userPrescriptionList.addAll(it.data)
                        userPrescriptionListAdapter.notifyDataSetChanged()
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    val vv = it.message
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }
}