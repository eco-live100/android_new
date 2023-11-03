package com.app.ecolive.pharmacy_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityMedicationListBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

class MedicationListActivity : BaseActivity() {
    lateinit var binding: ActivityMedicationListBinding
    private val progressDialog = CustomProgressDialog()
    var name : String? = null
    var address : String? = null
    var ssn : String? = null
    private var imagePath : String? = null
    private var insurancebody: MultipartBody.Part? = null
    lateinit var baseViewModel: PharmacyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medication_list)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        getCommonMedication()
        binding.toolbar.toolbarTitle.text = "Eco- Live Health Profile"
        binding.toolbar.help.visibility = View.VISIBLE
        if (intent.extras != null) {
            name = intent.getStringExtra("name")!!
            address = intent.getStringExtra("address")!!
            ssn = intent.getStringExtra("ssn")!!
            imagePath = intent.getStringExtra("imagePath")
        }
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            createHealthApi()
        }
    }


    private fun createHealthApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        var pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("name", name?:"")
        builder.addFormDataPart("address", address?:"")
        builder.addFormDataPart("ssn", ssn?:"")
        builder.addFormDataPart("medications", arrayListOf("test","test").toString())

        if (insurancebody != null) {
            setBodyInsurance()
            builder.addPart(insurancebody!!)
        }

        pharmacyViewModel.creatHealthProfile(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        PreferenceKeeper.instance.isHealthProfileCreate = true
                        startActivity(Intent(this, PharmacyStepActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
       // progressDialog.show(this)

        pharmacyViewModel.getCommonMedicationApi()
        pharmacyViewModel.getCommonMedicationData().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                   // progressDialog.dialog.dismiss()
                    it.data?.let {
                       // startActivity(Intent(this, PharmacyStepActivity::class.java))
                        //     finish()
                    }
                }
                Status.LOADING -> {
                    Timber.d( "LOADING: ")
                }
                Status.ERROR -> {
                 //   progressDialog.dialog.dismiss()
                    Timber.d("ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }
}