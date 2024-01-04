package com.app.ecolive.pharmacy_module.doctor

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityDoctorProfileBinding
import com.app.ecolive.pharmacy_module.HospitalUserActivity
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.ServiceListAdapter
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class DoctorProfile : AppCompatActivity() {
    lateinit var binding: ActivityDoctorProfileBinding
    private val progressDialog = CustomProgressDialog()
    lateinit var serviceListAdapter: ServiceListAdapter
    private var serviceList: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_profile)
        binding.add.setOnClickListener {
            startActivity(Intent(this, HospitalUserActivity::class.java))
        }
        binding.toolbarTitle.text = getString(R.string.profile)
        binding.ivBack.setOnClickListener {
            finish()
        }
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        binding.serviceRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceListAdapter = ServiceListAdapter(this, serviceList)
        binding.serviceRecyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = serviceListAdapter
        }

        //binding.serviceRecyclerView.adapter = serviceListAdapter
        getProfile()
    }

    private fun getProfile() {
        progressDialog.show(this)
        val pharmacyViewModel = PharmacyViewModel(this)
        /*        val userId = PreferenceKeeper.instance.loginResponse!!._id
                val professionType = "doctor"*/

        pharmacyViewModel.getProfile(/*userId = userId, professionType = professionType*/)
            .observe(this) { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialog.dialog.dismiss()
                        it.data?.data.let { doctorData ->
                            binding.apply {
                                doctorData?.let { data ->
                                    doctorNameTv.text = data.fullName.replaceFirstChar { it.uppercase() }
                                    doctorAddressTv.text = data.location
                                    "Consult fee $${data.consultFees}".also {
                                        consultFeeTv.text = it
                                    }
                                    Log.d("image_full_path", "${AppConstant.BASE_URL_Image}${data.logo}")
                                    Glide.with(this@DoctorProfile).load("${AppConstant.BASE_URL_Image}${data.logo}")
                                        .placeholder(R.drawable.ic_user_blue).centerCrop()
                                        .into(binding.doctorProfile)
                                    if (data.services.isNotEmpty()) {
                                        serviceList.addAll(data.services.split(","))
                                        serviceListAdapter.notifyDataSetChanged()
                                    }
                                    //var distance = distanceInMeter(s)
                                    binding.editProfileIcon.setOnClickListener {
                                        startActivity(
                                            Intent(
                                                this@DoctorProfile,
                                                CreateAndUpdateDoctorProfile::class.java
                                            )
                                                .putExtra(AppConstant.doctorProfile, data)
                                        )
                                    }
                                    binding.requestListTv.setOnClickListener {
                                        startActivity(Intent(this@DoctorProfile, RequestListDoctor::class.java)
                                            .putExtra(AppConstant.doctorId, data._id))
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
    private fun distanceInMeter(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Float {
        var results = FloatArray(1)
        Location.distanceBetween(startLat,startLon,endLat,endLon,results)
        return results[0]
    }
}