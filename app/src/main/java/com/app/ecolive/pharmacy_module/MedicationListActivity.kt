package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityMedicationListBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.model.CreateHealthModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.Status
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class MedicationListActivity : BaseActivity() {
    lateinit var binding: ActivityMedicationListBinding
    private val progressDialog = CustomProgressDialog()
    var name = ""
    var address = ""
    var ssn = ""
    var imagePath = ""
    var insurancebody: MultipartBody.Part? = null
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
            imagePath = intent.getStringExtra("imagePath")!!
        }
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            creatHealthApi()

//            startActivity(Intent(this, PharmacyStepActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        }

    }


    private fun creatHealthApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        var pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("name", name)
        builder.addFormDataPart("address", address)
        builder.addFormDataPart("ssn", ssn)
        builder.addFormDataPart("medications", arrayListOf<String>("test","test").toString())

        if (insurancebody != null) {
            setBodyInsurance()
            builder.addPart(insurancebody!!)
        }

        pharmacyViewModel.creatHealthProfile(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        PreferenceKeeper.instance.isHealthProfileCreate =true
                        startActivity(Intent(this, PharmacyStepActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                   //     finish()

                    }
                }
                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun setBodyInsurance() {
        val filePath = File(imagePath)
        val reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), filePath)
        insurancebody = MultipartBody.Part.createFormData("images", filePath.name, reqFile)

    }

    private fun getCommonMedication() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        var pharmacyViewModel = PharmacyViewModel(this)
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
                    Log.d("ok", "LOADING: ")
                }
                Status.ERROR -> {
                 //   progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }
}