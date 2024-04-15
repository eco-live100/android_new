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
import com.app.ecolive.pharmacy_module.PharmacyStepActivity
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.ServiceListAdapter
import com.app.ecolive.pharmacy_module.model.HealthProfileData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class PharmacyProcessActivity : AppCompatActivity() {
    lateinit var binding: ActivityPharmacyProcessBinding
    private val progressDialog = CustomProgressDialog()
    lateinit var listAdapter: ServiceListAdapter
    private var list: ArrayList<String> = ArrayList()
    val flexboxLayoutManager = FlexboxLayoutManager(this)
    var healthProfileData: HealthProfileData? = null

    companion object {
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
                ).putExtra(AppConstant.data, healthProfileData)
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
                        PharmacyStepActivity::class.java
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

        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

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
                Intent(this, CreateHealthActivity::class.java)
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
                        if (it.data?.data == null) {
                            binding.cardView8.visibility = View.GONE
                            binding.nextButton.text = "Create"
                            PreferenceKeeper.instance.isHealthProfileCreate = false
                        } else {
                            binding.cardView8.visibility = View.VISIBLE
                            binding.nextButton.text = "Next"
                            PreferenceKeeper.instance.isHealthProfileCreate = true
                            healthProfileData = it.data.data

                            binding.apply {
                                nameTv.text =
                                    healthProfileData?.name?.replaceFirstChar { it.uppercase() }
                                addressTv.text = healthProfileData?.address
                                list.clear()
                                healthProfileData?.commonMedication?.toList()
                                    ?.let { it1 -> list.addAll(it1.map { item -> item.medicineName}) }
                                listAdapter = ServiceListAdapter(this@PharmacyProcessActivity, list)
                                binding.recyclerView.apply {
                                    layoutManager = flexboxLayoutManager
                                    adapter = listAdapter
                                }
                                "Last 4 digits of SSN:- ${healthProfileData?.ssn}".also {
                                    ssnTv.text = it
                                }
                                if (healthProfileData?.insurance?.isNotEmpty() == true)
                                    Glide.with(this@PharmacyProcessActivity)
                                        .load("${AppConstant.BASE_URL_Image}/${healthProfileData?.insurance}")
                                        //.placeholder(R.drawable.bg_dash).centerCrop()
                                        .into(binding.insuranceImage)
                            }
                        }

                    }

                    Status.LOADING -> {}
                    Status.ERROR -> {
                        progressDialog.dialog.dismiss()
                        val vv = it.message
                        MyApp.popErrorMsg("", "" + vv, this)
                    }
                }

            }
    }
}