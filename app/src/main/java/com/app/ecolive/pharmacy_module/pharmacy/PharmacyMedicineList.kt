package com.app.ecolive.pharmacy_module.pharmacy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.PharmacyMedicineListBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.PharmacyMedicineListAdapter
import com.app.ecolive.pharmacy_module.model.MedicineDataModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils

class PharmacyMedicineList : AppCompatActivity() {
    lateinit var binding: PharmacyMedicineListBinding
    private lateinit var pharmacyMedicineListAdapter: PharmacyMedicineListAdapter
    private var medicineList: ArrayList<MedicineDataModel> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    var pharmacyId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.pharmacy_medicine_list)
        binding.toolbar.toolbarTitle.text = getString(R.string.my_medicine_s)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }


        intent.extras?.let {
            pharmacyId = it.getString(AppConstant.pharmacyId).toString()
        }
        binding.floatingButton.setOnClickListener {
            startActivity(
                Intent(this, AddMedicine::class.java).putExtra(
                    AppConstant.pharmacyId,
                    pharmacyId
                )
            )
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recycleView.layoutManager = layoutManager
        pharmacyMedicineListAdapter = PharmacyMedicineListAdapter(this, medicineList)
        binding.recycleView.adapter = pharmacyMedicineListAdapter

        getMedicineListApi()

        /*    binding.searchUserEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val searchedKey = binding.searchUserEditText.text.toString()
                    doctorListAdapter.filter.filter(searchedKey)
                }
                override fun afterTextChanged(s: Editable) {}
            })*/
    }

    private fun getMedicineListApi() {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)

        medicineList.clear()
        viewModel.getMedicineListApi(userId = pharmacyId).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        medicineList.addAll(it.data)
                        pharmacyMedicineListAdapter.notifyDataSetChanged()
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