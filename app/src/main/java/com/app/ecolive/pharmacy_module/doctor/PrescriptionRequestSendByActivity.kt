package com.app.ecolive.pharmacy_module.doctor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescriptionRequestSendByBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.ServiceListAdapter
import com.app.ecolive.pharmacy_module.model.PrescriptionDataModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import org.json.JSONObject
import timber.log.Timber

class PrescriptionRequestSendByActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrescriptionRequestSendByBinding
    private val progressDialog = CustomProgressDialog()

    lateinit var serviceListAdapter: ServiceListAdapter
    private var medicineList: ArrayList<String> = ArrayList()
    private var requestData: PrescriptionDataModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_prescription_request_send_by)
        binding.toolbar.toolbarTitle.text ="Prescription request sent by"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.prescribeBtn.setOnClickListener {
           startActivity(Intent(this, StartPrescribingActivity::class.java))
        }
        binding.declineRequestBtn.setOnClickListener {
           cancelPrescriptionApi()
        }
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        intent.extras?.let {
            requestData =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getSerializable(
                        AppConstant.data,
                        PrescriptionDataModel::class.java
                    )
                } else {
                    it.getSerializable(AppConstant.data) as PrescriptionDataModel
                }
            binding.symptomsTv.text = requestData?.symptomDescription
            binding.symptomDurationTv.text = requestData?.symptomDuration
            binding.prescriptionTv.text = requestData?.alreadyMedication
            binding.medicalHistoryTv.text = requestData?.recentMedicalHistory
            binding.allergiesTv.text = requestData?.allergies
            binding.smokingTv.text = requestData?.habits
            binding.otherTv.text = requestData?.otherRelaventInformation

            Glide.with(this).load("${AppConstant.BASE_URL_Image}${requestData?.attachment}")
                .placeholder(R.drawable.ic_user_blue).centerCrop()
                .into(binding.doc1Iv)
            Glide.with(this).load("${AppConstant.BASE_URL_Image}${requestData?.picture}")
                .placeholder(R.drawable.ic_user_blue).centerCrop()
                .into(binding.doc2Iv)

        }
        medicineList.add("Nice")
        medicineList.add("Esprin")
        medicineList.add("Bufen")
        medicineList.add("Dolo")
        medicineList.add("Paracitamol")
        medicineList.add("Elaresy")
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        serviceListAdapter = ServiceListAdapter(this, medicineList)
        binding.recyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = serviceListAdapter
        }
    }

    private fun cancelPrescriptionApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val jsonObject = JSONObject()
        jsonObject.put("precriptionId","precriptionId")

        pharmacyViewModel.cancelPrescriptionApi(jsonObject).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {item->
                        Toast.makeText(this,item.message,Toast.LENGTH_SHORT).show()
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

    private fun prescriptionDetailApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val jsonObject = JSONObject()
        jsonObject.put("prescriptionId","precriptionId")

        pharmacyViewModel.prescriptionDetailApi(jsonObject).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {item->
                        Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
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
}
