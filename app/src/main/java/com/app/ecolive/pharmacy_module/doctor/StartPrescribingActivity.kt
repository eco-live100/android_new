package com.app.ecolive.pharmacy_module.doctor

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityStartPrescribingBinding
import com.app.ecolive.databinding.CustomAddMedicineDialogBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.MedicineListByDoctorAdapter
import com.app.ecolive.pharmacy_module.model.PrescriptionMedicationData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber


class StartPrescribingActivity : AppCompatActivity() {
    lateinit var binding: ActivityStartPrescribingBinding
    private val progressDialog = CustomProgressDialog()
    lateinit var dialog: Dialog
    private lateinit var medicineListByDoctorAdapter: MedicineListByDoctorAdapter
    private var list: ArrayList<PrescriptionMedicationData> = ArrayList()
    private lateinit var prescriptionId: String

    companion object {
        private var mInstance: StartPrescribingActivity? = null
    }

    fun getInstance(): StartPrescribingActivity? {
        return mInstance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_prescribing)
        binding.toolbar.toolbarTitle.text = "Start prescribing now"
        binding.toolbar.help.visibility = View.VISIBLE
        mInstance = this
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        dialog = Dialog(this)
        binding.addMedicineBtn.setOnClickListener {
            addPrescriptionMedicine()
        }
        intent.extras?.let {
            prescriptionId = it.getString(AppConstant.prescriptionId).toString()
            binding.patientName.text = it.getString(AppConstant.name).toString().capitalize()
            binding.patientAddress.text = it.getString(AppConstant.address).toString().capitalize()
            val image = it.getString(AppConstant.image).toString()
            Glide.with(this).load("${AppConstant.BASE_URL_Image}${image}")
                .placeholder(R.drawable.ic_user_blue).centerCrop()
                .into(binding.userImage)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        medicineListByDoctorAdapter = MedicineListByDoctorAdapter(this, list)
        binding.recyclerView.adapter = medicineListByDoctorAdapter

        binding.sendPrescriptionToPatient.setOnClickListener {
            startPrescriptionApi()
        }
    }

    private fun startPrescriptionApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val jsArray = JSONArray(list)
        val jsonObject = JSONObject()
        jsonObject.put("prescriptionId", prescriptionId)
        jsonObject.put("Medication", jsArray)

        pharmacyViewModel.startPrescriptionApi(jsonObject).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let { item ->
                        Toast.makeText(this, item.message, Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(this, DoctorProfile::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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

    private fun addPrescriptionMedicine() {
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val customAddMedicineDialogBinding: CustomAddMedicineDialogBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.custom_add_medicine_dialog, null, false
            )
        dialog.setContentView(customAddMedicineDialogBinding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        customAddMedicineDialogBinding.apply {
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
            addButton.setOnClickListener {
                list.add(
                    PrescriptionMedicationData(
                        medicineName = nameEt.text.toString(),
                        strength = strengthEt.text.toString(),
                        dose = doseEt.text.toString(),
                        route = routeEt.text.toString(),
                        frequency = frequencyEt.text.toString(),
                        refills = refillsEt.text.toString(),
                        indication = indicationEt.text.toString(),
                        additionalDirections = additionalDirectionEt.text.toString(),
                    )
                )
                medicineListByDoctorAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}