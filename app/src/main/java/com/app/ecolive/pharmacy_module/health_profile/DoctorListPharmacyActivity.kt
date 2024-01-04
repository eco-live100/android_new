package com.app.ecolive.pharmacy_module.health_profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityDoctorListPhramicyBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.DoctorListAdapter
import com.app.ecolive.pharmacy_module.model.DoctorListModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils

class DoctorListPharmacyActivity : AppCompatActivity() {
    lateinit var binding: ActivityDoctorListPhramicyBinding
    lateinit var doctorListAdapter : DoctorListAdapter
    private var doctorList: ArrayList<DoctorListModel.Data> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_list_phramicy)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text = getString(R.string.doctor_list_eco_live)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recycleFriends.layoutManager = layoutManager
        doctorListAdapter = DoctorListAdapter(this, doctorList)
        binding.recycleFriends.adapter = doctorListAdapter

        getDoctorListApi()

        binding.searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchedKey = binding.searchUserEditText.text.toString()
                doctorListAdapter.filter.filter(searchedKey)
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getDoctorListApi() {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)

        doctorList.clear()
        viewModel.getDoctorListApi().observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        binding.totalList.text = "Total ${it.data.size} in this list"
                        doctorList.addAll(it.data)
                        doctorListAdapter.notifyDataSetChanged()
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