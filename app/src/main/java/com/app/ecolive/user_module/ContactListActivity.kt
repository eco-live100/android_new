package com.app.ecolive.user_module

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ContactlistActivityBinding
import com.app.ecolive.msg_module.DialActivity
import com.app.ecolive.msg_module.VoipActivity
import com.app.ecolive.payment_module.model.Contact
import com.app.ecolive.service.Status
import com.app.ecolive.user_module.user_adapter.ContactListAdapter
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.PaymentViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ContactListActivity : AppCompatActivity() {
    lateinit var binding: ContactlistActivityBinding
    var contactsListAdapter: ContactListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@ContactListActivity, R.layout.contactlist_activity)
        contactsListAdapter =
            ContactListAdapter(
                arrayListOf(),
                arrayListOf(),
                object : ContactListAdapter.ClickListener {

                    override fun onClick(data: Contact) {
                        startActivity(
                            Intent(
                                this@ContactListActivity,
                                VoipActivity::class.java
                            ).putExtra("mobile", data.mobile)
                                .putExtra("name",data.name)
                        )

                    }


                })
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
        binding.contactSearctedt.doAfterTextChanged { contactsListAdapter?.filter?.filter(it) }
    }


    private fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = this.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val contactThumbnailIndex =
                contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val contactThumbnail = contactsCursor.getString(contactThumbnailIndex)
                val phoneCursor = this.applicationContext?.contentResolver?.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    arrayOf(id),
                    null
                )

                if (phoneCursor != null && phoneCursor.count > 0) {
                    val numberIndex =
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (phoneCursor.moveToNext()) {
                        val number: String = phoneCursor.getString(numberIndex)
                        //check if the map contains key or not, if not then create a new array list with number
                        if (name != null) {
                            val contact = Contact(id = id, name = name, imageUri = contactThumbnail)
                            contact.mobile = number
                            contactsList.add(contact)
                        }
                    }
                    //contact contains all the number of a particular contact
                    phoneCursor.close()
                }

            }
            contactsCursor.close()
        }
        binding.isShimmerShow = false
        return contactsList
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
        CoroutineScope(Dispatchers.Main).launch {

            contactsListAdapter?.update(getPhoneContacts())

        }

    }


}