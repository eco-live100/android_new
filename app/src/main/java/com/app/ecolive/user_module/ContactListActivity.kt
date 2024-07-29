package com.app.ecolive.user_module

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ContactlistActivityBinding
import com.app.ecolive.msg_module.VoipActivity
import com.app.ecolive.user_module.user_adapter.ContactListAdapter
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.ContactViewModel
import com.app.ecolive.viewmodel.PaymentViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class ContactListActivity : AppCompatActivity() {
    lateinit var binding: ContactlistActivityBinding
    var contactsListAdapter: ContactListAdapter? = null
    private lateinit var paymentViewModel :PaymentViewModel
    lateinit var viewModel: ContactViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@ContactListActivity, R.layout.contactlist_activity)
          viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        contactsListAdapter =
            ContactListAdapter(
                arrayListOf(),
                arrayListOf(),
                object : ContactListAdapter.ClickListener {

                    override fun onClick(name: String, numbers: String) {
                        startActivity(
                            Intent(
                                this@ContactListActivity,
                                VoipActivity::class.java
                            ).putExtra("mobile",  numbers)
                                .putExtra("name",name)
                        )

                    }


                })
        paymentViewModel= PaymentViewModel(this)
        binding.contactListRecycle.apply {
            layoutManager = LinearLayoutManager(this@ContactListActivity)
            adapter = contactsListAdapter
        }
        setToolBar()
        binding.isShimmerShow = true
        checkPermissions()
    }


    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)

        binding.toolbarContactList.toolbarTitle.text = "Contacts"
        binding.toolbarContactList.ivBack.setOnClickListener { finish() }
        binding.dialFab.setOnClickListener {
            startActivity(
                Intent(
                    this@ContactListActivity,
                    VoipActivity::class.java
                )
            )
        }
      //  binding.contactSearctedt.doAfterTextChanged { contactsListAdapter?.filter?.filter(it) }
    }




    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_CONTACTS
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            callList()
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@ContactListActivity,
                                "permissions are required to continue",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_CONTACTS

                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            callList()
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@ContactListActivity,
                                "permissions are required to continue",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }
    }

    private fun callList() {
        viewModel.contactsLiveData.observe(this) {
            binding.isShimmerShow =false
            contactsListAdapter?.update(it)
        }
        viewModel.fetchContacts()


    }


}