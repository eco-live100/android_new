package com.app.ecolive.pharmacy_module.pharmacy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyProfileBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class PharmacyProfile : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyProfileBinding
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pharmacy_profile)
        binding.ivBack.setOnClickListener {
            finish()
        }
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }

        getProfile()
    }

    private fun getProfile() {
        progressDialog.show(this)
        val pharmacyViewModel = PharmacyViewModel(this)

        pharmacyViewModel.getPharmacyProfile()
            .observe(this) { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialog.dialog.dismiss()
                        it.data?.data.let { doctorData ->
                            binding.apply {
                                doctorData?.let { data ->
                                    pharmacyNameTv.text =
                                        data.pharmacyName.replaceFirstChar { it.uppercase() }
                                    pharmacyAddressTv.text = data.location
                                    mobileNumberTv.text = data.mobileNumber
                                    "Open: ${data.openingFrom}".also { openStoreTimeTv.text = it }
                                    "Close: ${data.openingTo}".also { closeStoreTimeTv.text = it }
                                    "Today order count\n ${data.todaysOrder}".also {
                                        todayOrderCountTv.text = it
                                    }
                                    Glide.with(this@PharmacyProfile)
                                        .load("${AppConstant.BASE_URL_Image}${data.pharmacyImage}")
                                        .placeholder(R.drawable.doctor_bg).centerCrop()
                                        .into(binding.pharmacyImageView)
                                    Glide.with(this@PharmacyProfile)
                                        .load("${AppConstant.BASE_URL_Image}${data.licenceImage}")
                                        .placeholder(R.drawable.doctor_bg).centerCrop()
                                        .into(binding.documentImageView1)
                                    //var distance = distanceInMeter(s)
                                    binding.editProfileIcon.setOnClickListener {
                                        startActivity(
                                            Intent(
                                                this@PharmacyProfile,
                                                CreateAndUpdatePharmacyProfile::class.java
                                            ).putExtra(AppConstant.profile, data)
                                        )
                                    }
                                    binding.myMedicineLL.setOnClickListener {
                                        startActivity(
                                            Intent(
                                                this@PharmacyProfile,
                                                PharmacyMedicineList::class.java
                                            ).putExtra(AppConstant.pharmacyId, data._id)
                                        )
                                    }
                                    binding.myRequestLL.setOnClickListener {
                                        startActivity(
                                            Intent(
                                                this@PharmacyProfile,
                                                RequestListPharmacy::class.java
                                            ).putExtra(AppConstant.pharmacyId, data._id)
                                        )
                                    }
                                    binding.pharmacyStatusSwitch.isChecked = data.activeStatus
                                    riderSwitchButton(binding.pharmacyStatusSwitch.isChecked )
                                    binding.pharmacyStatusSwitch.setOnClickListener {
                                        updatePharmacyStatusApi(pharmacyId=data._id)
                                    }
                                }
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        progressDialog.dialog.dismiss()
                        val vv = it.message
                        MyApp.popErrorMsg("", "" + vv, this)
                        // MyApp.popErrorMsg("", "" + vv, THIS!!)
                    }
                }
            }
    }

    private fun updatePharmacyStatusApi(pharmacyId: String) {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)

        viewModel.updatePharmacyStatusApi(pharmacyId = pharmacyId).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {item->
                        riderSwitchButton(status=item.data.activeStatus)
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

    private fun riderSwitchButton(status: Boolean ) {
        binding.pharmacyStatusSwitch.isChecked = status
        if (status) {
            binding.pharmacyStatusSwitch.text = getString(R.string.open)
            binding.pharmacyStatusSwitch.setTextColor(
                resources.getColor(
                    R.color.color_006400
                )
            )
        } else {
            binding.pharmacyStatusSwitch.text = getString(R.string.close)
            binding.pharmacyStatusSwitch.setTextColor(
                resources.getColor(
                    R.color.color_red
                )
            )
        }
    }
}