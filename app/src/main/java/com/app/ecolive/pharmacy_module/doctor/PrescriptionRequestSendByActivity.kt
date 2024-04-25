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
import com.app.ecolive.pharmacy_module.adapter.MedicineListByDoctorAdapter
import com.app.ecolive.pharmacy_module.adapter.ServiceListAdapter
import com.app.ecolive.pharmacy_module.model.PharmacyOrderListModel
import com.app.ecolive.pharmacy_module.model.PrescriptionDataModel
import com.app.ecolive.pharmacy_module.model.PrescriptionMedicationData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.toast
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale

class PrescriptionRequestSendByActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrescriptionRequestSendByBinding
    private val progressDialog = CustomProgressDialog()

    lateinit var serviceListAdapter: ServiceListAdapter
    private var medicineList: ArrayList<String> = ArrayList()
    private var requestData: PrescriptionDataModel? = null
    private var pharmacyOrderListModel: PharmacyOrderListModel? = null

    private lateinit var medicineListByDoctorAdapter: MedicineListByDoctorAdapter
    private var list: ArrayList<PrescriptionMedicationData> = ArrayList()

    var orderStatus: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_prescription_request_send_by)
        binding.toolbar.toolbarTitle.text = "Prescription request sent by"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.declineRequestBtn.setOnClickListener {
            updateStatus(2) ///2-Cancelled or , 2-DriverAccept
        }
        binding.readyToDispatchedButton.setOnClickListener {
            if (orderStatus==1 && binding.totalAmountEt.text?.isEmpty() == true) {
               binding.totalAmountEt.error = "Please enter amount"
                binding.totalAmountEt.requestFocus()
            } else if (orderStatus==1 && binding.totalAmountEt.text.toString().toDouble() <= 0) {
                toast("Please enter valid amount")
            }else {
                updateStatus(orderStatus)
            }///0-acceptedByPharmacy or 0-placed
        }
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        intent.extras?.let {
            var fromScreen = it.getString(AppConstant.fromScreen)

            if (fromScreen == "pharmacy") {
                binding.readyToDispatchedButton.visibility = View.VISIBLE
                binding.prescribeBtn.visibility = View.GONE
                binding.declineRequestBtn.visibility = View.GONE
                pharmacyOrderListModel =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getSerializable(
                            AppConstant.data,
                            PharmacyOrderListModel::class.java
                        )
                    } else {
                        it.getSerializable(AppConstant.data) as PharmacyOrderListModel
                    }
                if (pharmacyOrderListModel?.deliveryStatus == 0) {//0-placed
                    binding.readyToDispatchedButton.text = AppConstant.readyToDispatched
                    orderStatus = 1
                } else if (pharmacyOrderListModel?.deliveryStatus == null) {
                    binding.readyToDispatchedButton.text = AppConstant.acceptOrder
                    orderStatus = 0
                } else /*if(pharmacyOrderListModel?.deliveryStatus==null)*/ {
                    binding.readyToDispatchedButton.visibility = View.GONE
                    orderStatus = 0
                }

                val user = pharmacyOrderListModel?.user
                if (user != null) {
                    binding.patientNameTv.text =
                        "${user.firstName} ${user.lastName}"?.capitalize(Locale.ROOT)
                    Glide.with(this).load("${AppConstant.BASE_URL_Image}${user.profilePicture}")
                        .placeholder(R.drawable.ic_user_blue).centerCrop()
                        .into(binding.patientImage)
                }
                pharmacyOrderListModel?.healthProfile?.commonMedication?.toList()
                    ?.let { it1 -> medicineList.addAll(it1.map { item -> item.medicineName }) }

                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                serviceListAdapter = ServiceListAdapter(this, medicineList)
                binding.recyclerView.apply {
                    layoutManager = flexboxLayoutManager
                    adapter = serviceListAdapter
                }

                if (pharmacyOrderListModel?.healthProfile?.insurance?.isNotEmpty() == true)
                    Glide.with(this)
                        .load("${AppConstant.BASE_URL_Image}/${pharmacyOrderListModel?.healthProfile?.insurance}")
                        //.placeholder(R.drawable.bg_dash).centerCrop()
                        .into(binding.insuranceIv)

                requestData = pharmacyOrderListModel?.precriptionDetails
                requestData?.let { it.Medication?.let { it1 -> list.addAll(it1) } }

               // binding.doctorPrescriptionLL.visibility = View.GONE

                binding.doctorPrescriptionLL.visibility = View.VISIBLE

                val layoutManager = LinearLayoutManager(this)
                binding.recyclerViewMedication.layoutManager = layoutManager
                medicineListByDoctorAdapter = MedicineListByDoctorAdapter(this, list)
                binding.recyclerViewMedication.adapter = medicineListByDoctorAdapter

                binding.totalAmountLL.visibility = View.VISIBLE
            } else {
                binding.totalAmountLL.visibility = View.GONE
                binding.doctorPrescriptionLL.visibility = View.GONE
                binding.readyToDispatchedButton.visibility = View.GONE
                binding.prescribeBtn.visibility = View.VISIBLE
                binding.declineRequestBtn.visibility = View.VISIBLE
                requestData =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getSerializable(
                            AppConstant.data,
                            PrescriptionDataModel::class.java
                        )
                    } else {
                        it.getSerializable(AppConstant.data) as PrescriptionDataModel
                    }

                requestData?.healthProfile?.commonMedication?.toList()
                    ?.let { it1 -> medicineList.addAll(it1.map { item -> item.medicineName }) }

                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                serviceListAdapter = ServiceListAdapter(this, medicineList)
                binding.recyclerView.apply {
                    layoutManager = flexboxLayoutManager
                    adapter = serviceListAdapter
                }

                if (requestData?.status == 1) {//0-placed
                    binding.prescribeBtn.visibility = View.GONE
                    binding.declineRequestBtn.visibility = View.GONE
                    binding.statusTv.text = "Order Completed"
                    binding.statusTv.setTextColor(resources.getColor(R.color.color_006400))
                } else if (requestData?.status == 2) {
                    binding.prescribeBtn.visibility = View.GONE
                    binding.declineRequestBtn.visibility = View.GONE
                } else {
                    binding.prescribeBtn.visibility = View.VISIBLE
                    binding.declineRequestBtn.visibility = View.VISIBLE
                }
            }

            binding.symptomsTv.text = requestData?.symptomDescription
            binding.symptomsDurationTv.text = requestData?.symptomDuration
            binding.prescriptionMedicationTv.text = requestData?.alreadyMedication
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


            binding.prescribeBtn.setOnClickListener {
                startActivity(
                    Intent(this, StartPrescribingActivity::class.java)
                        .putExtra(AppConstant.prescriptionId, requestData?._id)
                        .putExtra(
                            AppConstant.name,
                            requestData?.patientDetails?.firstName + " " + requestData?.patientDetails?.lastName
                        )
                        .putExtra(AppConstant.image, requestData?.patientDetails?.profilePicture)
                        .putExtra(AppConstant.address, requestData?.patientDetails?.address ?: "")
                )
            }

        }
        val user = requestData?.patientDetails
        if (user != null) {
            binding.patientNameTv.text =
                "${user.firstName} ${user.lastName}"?.capitalize(Locale.ROOT)
            Glide.with(this).load("${AppConstant.BASE_URL_Image}${user.profilePicture}")
                .placeholder(R.drawable.ic_user_blue).centerCrop()
                .into(binding.patientImage)
        }

        requestData?.healthProfile?.commonMedication?.toList()
            ?.let { it1 -> medicineList.addAll(it1.map { item -> item.medicineName }) }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        serviceListAdapter = ServiceListAdapter(this, medicineList)
        binding.recyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = serviceListAdapter
        }

        if (requestData?.healthProfile?.insurance?.isNotEmpty() == true)
            Glide.with(this)
                .load("${AppConstant.BASE_URL_Image}/${requestData?.healthProfile?.insurance}")
                //.placeholder(R.drawable.bg_dash).centerCrop()
                .into(binding.insuranceIv)


    }

    private fun updateStatus(status: Int?) {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val jsonObject = JSONObject()
        jsonObject.put(AppConstant.pharmacyId, pharmacyOrderListModel?.pharmacyId)
        jsonObject.put(AppConstant.orderId, pharmacyOrderListModel?._id)
        jsonObject.put(AppConstant.orderStatus, status)

        pharmacyViewModel.updateMedicalOrderByPharmacy(jsonObject).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let { item ->
                        Toast.makeText(this, item.message, Toast.LENGTH_SHORT).show()
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

}
