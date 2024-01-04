package com.app.ecolive.pharmacy_module.doctor

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import org.json.JSONObject
import timber.log.Timber

class StartPrescribingActivity : AppCompatActivity() {
    lateinit var binding: ActivityStartPrescribingBinding
    private val progressDialog = CustomProgressDialog()
    lateinit var dialog: Dialog
    private lateinit var medicineListByDoctorAdapter: MedicineListByDoctorAdapter
    private var list: ArrayList<PrescriptionMedicationData> = ArrayList()

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
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        medicineListByDoctorAdapter = MedicineListByDoctorAdapter(this, list)
        binding.recyclerView.adapter = medicineListByDoctorAdapter

    }

    private fun startPrescriptionApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val jsonObject = JSONObject()
        jsonObject.put("prescriptionId", "precriptionId")
        jsonObject.put("strength", "precriptionId")
        jsonObject.put("dose", "precriptionId")
        jsonObject.put("route", "precriptionId")
        jsonObject.put("frequency", "precriptionId")
        jsonObject.put("refills", "precriptionId")
        jsonObject.put("quantify", "precriptionId")
        jsonObject.put("indication", "precriptionId")
        jsonObject.put("additionalDirections", "precriptionId")

        pharmacyViewModel.startPrescriptionApi(jsonObject).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let { item ->
                        Toast.makeText(this, item.message, Toast.LENGTH_SHORT).show()
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
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val customAddMedicineDialogBinding: CustomAddMedicineDialogBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.custom_add_medicine_dialog, null, false
            )
        dialog.setContentView(customAddMedicineDialogBinding.root);
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                        additionalDirection = additionalDirectionEt.text.toString(),
                    )
                )
                medicineListByDoctorAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}