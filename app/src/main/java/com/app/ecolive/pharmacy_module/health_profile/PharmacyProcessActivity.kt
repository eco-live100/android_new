package com.app.ecolive.pharmacy_module.health_profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyProcessBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.ServiceListAdapter
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class PharmacyProcessActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyProcessBinding
    private val progressDialog = CustomProgressDialog()
    lateinit var listAdapter: ServiceListAdapter
    private var list: ArrayList<String> = ArrayList()
    companion object{
        var isUpdateProfile = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pharmacy_process)
        binding.toolbar.toolbarTitle.text = getString(R.string.health_profile)
        binding.toolbar.help.visibility = View.GONE
        binding.skip.setOnClickListener {
            showDialogHealthAlert()
        }
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.updateHealthProfile.setOnClickListener {
            isUpdateProfile = true
            startActivity(
                Intent(
                    this,
                    CreateHealthActivity::class.java
                )
            )
        }
        if (PreferenceKeeper.instance.isHealthProfileCreate) {
            binding.cardView8.visibility = View.VISIBLE
        } else {
            binding.cardView8.visibility = View.GONE
        }

        binding.nextButton.setOnClickListener {
            if (PreferenceKeeper.instance.isHealthProfileCreate) {
                startActivity(
                    Intent(
                        this,
                        SearchMedicinesActivity::class.java
                    )
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        CreateHealthActivity::class.java
                    )
                )
            }
        }
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        list.add("ABC")
        list.add("XYZ")
        list.add("Dola-350")
        list.add("Paracetamol")
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        listAdapter = ServiceListAdapter(this, list)
        binding.recyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = listAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        isUpdateProfile = false
        getHealthProfile()
    }

    private fun showDialogHealthAlert() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_skip_pharmicy)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );

        val goBack = dialog.findViewById(R.id.goBack) as TextView
        val ok = dialog.findViewById(R.id.ok) as AppCompatButton

        ok.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this,
                    CreateHealthActivity::class.java
                )
            )
        }

        goBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun getHealthProfile() {
        progressDialog.show(this)
        val pharmacyViewModel = PharmacyViewModel(this)
        pharmacyViewModel.getHealthProfile()
            .observe(this) { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialog.dialog.dismiss()
                        it.data?.data.let { doctorData ->
                            binding.apply {
                                doctorData?.let { data ->
                                    val item = data.last()
                                    nameTv.text = item.name.replaceFirstChar { it.uppercase() }
                                    addressTv.text = item.address
                                    "Last 4 digits of SSN:- ${item.ssn}".also { ssnTv.text = it }
                                    /*  Glide.with(this@PharmacyProcessActivity).load("${AppConstant.BASE_URL_Image}${data.logo}")
                                          .placeholder(R.drawable.ic_user_blue).centerCrop()
                                          .into(binding.doctorProfile)*/

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
}