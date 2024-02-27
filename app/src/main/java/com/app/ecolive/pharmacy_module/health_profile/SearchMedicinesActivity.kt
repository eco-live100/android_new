package com.app.ecolive.pharmacy_module.health_profile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivitySearchMedicinesBinding
import com.app.ecolive.pharmacy_module.PharmacyStepActivity
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.SearchMedicineListAdapter
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import com.app.ecolive.pharmacy_module.model.SearchMedicineListData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

class SearchMedicinesActivity : BaseActivity() {
    lateinit var binding: ActivitySearchMedicinesBinding
    private val progressDialog = CustomProgressDialog()
    var name: String? = null
    var address: String? = null
    var ssn: String? = null
    private var imagePath: String? = null
    private var insurancebody: MultipartBody.Part? = null

    lateinit var medicineListAdapter: SearchMedicineListAdapter
    private var medicineList: ArrayList<CommonMedicationModel.Data> = ArrayList()

    lateinit var searchMedicineListAdapter: SearchMedicineListAdapter
    private var searchMedicineList: ArrayList<SearchMedicineListData> = ArrayList()

    val flexboxSearchLayoutManager = FlexboxLayoutManager(this)
    val flexboxLayoutManager = FlexboxLayoutManager(this)

    companion object {
        var mInstance: SearchMedicinesActivity? = null
        var selectedMedicineList: ArrayList<SearchMedicineListData> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_medicines)
        mInstance = this
        getCommonMedication()
        binding.toolbar.toolbarTitle.text = "Search Medicines"
        binding.toolbar.help.visibility = View.VISIBLE
        if (intent.extras != null) {
            name = intent.getStringExtra("name")!!
            address = intent.getStringExtra("address")!!
            ssn = intent.getStringExtra("ssn")!!
            imagePath = intent.getStringExtra("imagePath")
        }
        Log.d("TAG", "images_path:-$imagePath")
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        medicineListAdapter =
            SearchMedicineListAdapter(this, selectedMedicineList)
        binding.medicineRecyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = medicineListAdapter
        }

        flexboxSearchLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        searchMedicineListAdapter =
            SearchMedicineListAdapter(this, searchMedicineList)
        binding.searchMedicineRecyclerView.apply {
            layoutManager = flexboxSearchLayoutManager
            adapter = searchMedicineListAdapter
        }
        binding.btnContinue.setOnClickListener {
         /*   if (PreferenceKeeper.instance.isHealthProfileCreate && !PharmacyProcessActivity.isUpdateProfile) {
                startActivity(
                    Intent(this, PharmacyStepActivity::class.java)
                    //.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            } else {*/
                createHealthApi()
           // }
        }
        binding.clearIv.visibility = View.GONE
        binding.clearIv.setOnClickListener { binding.searchEt.text.clear() }
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.searchEt.text.isEmpty()) {
                    binding.clearIv.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    searchMedicineList.clear()
                    searchMedicineListAdapter.notifyDataSetChanged()
                } else {
                    binding.clearIv.visibility = View.VISIBLE
                }
                if (binding.searchEt.text.isNotEmpty() && binding.searchEt.text.length >= 3)
                    searchMedication(binding.searchEt.text.toString())
            }
        })
    }

    private fun createHealthApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("name", name ?: "")
        builder.addFormDataPart("address", address ?: "")
        builder.addFormDataPart("ssn", ssn ?: "")
        builder.addFormDataPart("medications", selectedMedicineList.toString())

        if (imagePath != null)
            setBodyInsurance()
        if (insurancebody != null) {
            builder.addPart(insurancebody!!)
        }
        pharmacyViewModel.createHealthProfile(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        PreferenceKeeper.instance.isHealthProfileCreate = true
                        PharmacyProcessActivity.isUpdateProfile = false
                        startActivity(
                            Intent(this, PharmacyStepActivity::class.java)
                            //.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }
                }

                Status.LOADING -> {
                    Timber.d("LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Timber.d("ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun setBodyInsurance() {
        //val filePath = imagePath?.let { it1 -> getFilePath(this, it1) }
        val file = File(imagePath)
        val reqFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        insurancebody = MultipartBody.Part.createFormData("images", file.name, reqFile)
    }

    private fun getCommonMedication() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        //progressDialog.show(this)
        pharmacyViewModel.getCommonMedicationApi().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    // progressDialog.dialog.dismiss()
                    it.data?.let { data ->
                        medicineList.clear()
                        // medicineList.addAll(data)
                        searchMedicineListAdapter.notifyDataSetChanged()
                    }
                }

                Status.LOADING -> {
                    Timber.d("LOADING: ")
                }

                Status.ERROR -> {
                    //progressDialog.dialog.dismiss()
                    Timber.d("ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun searchMedication(searchText: String) {
        val pharmacyViewModel = PharmacyViewModel(this)
        //progressDialog.show(this)
        pharmacyViewModel.searchMedicineApi(search = searchText).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { data ->
                        searchMedicineList.clear()
                        searchMedicineList.addAll(data.data)
                        searchMedicineListAdapter.notifyDataSetChanged()
                    }
                }

                Status.LOADING -> {
                    Timber.d("LOADING: ")
                }

                Status.ERROR -> {
                    //progressDialog.dialog.dismiss()
                    Timber.d("ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }



    fun getInstance(): SearchMedicinesActivity? {
        return mInstance
    }
    fun selectMedicine(item: SearchMedicineListData) {
        if (selectedMedicineList.contains(item))
            selectedMedicineList.remove(item)
        else
            selectedMedicineList.add(item)

        medicineListAdapter.notifyDataSetChanged()
    }
}