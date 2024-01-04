package com.app.ecolive.pharmacy_module.health_profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescribedMedicationsBinding
import com.app.ecolive.pharmacy_module.PharmacyStepActivity
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.MedicineListByDoctorAdapter
import com.app.ecolive.pharmacy_module.model.PrescriptionMedicationData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import org.json.JSONObject
import timber.log.Timber

class PrescribedMedicationsActivity : AppCompatActivity() {
    lateinit var binding :ActivityPrescribedMedicationsBinding
    private lateinit var medicineListByDoctorAdapter: MedicineListByDoctorAdapter
    private var list: ArrayList<PrescriptionMedicationData> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_prescribed_medications)
        binding.toolbar.toolbarTitle.text ="Medical Prescription"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        medicineListByDoctorAdapter = MedicineListByDoctorAdapter(this, list)
        binding.recyclerView.adapter = medicineListByDoctorAdapter
        binding.declineRequestButton.setOnClickListener {
            cancelPrescriptionApi()
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
                    it.data?.let {
                        PreferenceKeeper.instance.isHealthProfileCreate = true
                        startActivity(
                            Intent(this, PharmacyStepActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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