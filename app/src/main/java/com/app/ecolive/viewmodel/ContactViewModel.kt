package com.app.ecolive.viewmodel

import android.app.Application
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecolive.payment_module.model.Contact
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class ContactViewModel(val  mapplication: Application) : AndroidViewModel(mapplication) {


    private val _contactsLiveData = MutableLiveData<ArrayList<Contact>>()
    val contactsLiveData:LiveData<ArrayList<Contact>> = _contactsLiveData


//    private fun getPhoneContacts(): ArrayList<Contact> {
//        val contactsList = ArrayList<Contact>()
//        val contactsCursor = context.contentResolver?.query(
//            ContactsContract.Contacts.CONTENT_URI,
//            null,
//            null,
//            null,
//            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
//        )
//
//        if (contactsCursor != null && contactsCursor.count > 0) {
//            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
//            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
//            val contactThumbnailIndex =
//                contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
//            while (contactsCursor.moveToNext()) {
//                val id = contactsCursor.getString(idIndex)
//                val name = contactsCursor.getString(nameIndex)
//                val contactThumbnail = contactsCursor.getString(contactThumbnailIndex)
//                val phoneCursor = context.applicationContext?.contentResolver?.query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    null,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
//                    arrayOf(id),
//                    null
//                )
//
//                if (phoneCursor != null && phoneCursor.count > 0) {
//                    val numberIndex =
//                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                    while (phoneCursor.moveToNext()) {
//                        val number: String = phoneCursor.getString(numberIndex)
//                        //check if the map contains key or not, if not then create a new array list with number
//                        if (name != null) {
//                            val contact = Contact(id = id, name = name, imageUri = contactThumbnail)
//                            var mobile = number.replace(" ", "")
//                            var mobile2 = mobile.replace("+", "")
//                            contact.mobile = mobile2
//
//                            var isEqual =false
//
//                            if (contactsList.size==0){
//                                contactsList.add(contact)
//                            }else{
//                                for (i in 0 until contactsList.size) {
//                                    if (contactsList[i].mobile!! == mobile2){
//                                        isEqual =true
//                                        break
//                                    }
//                                }
//                                if (!isEqual){
//                                    contactsList.add(contact)
//                                }
//                            }
//
//                        }
//                    }
//                    //contact contains all the number of a particular contact
//                    phoneCursor.close()
//                }
//
//            }
//            contactsCursor.close()
//        }
//
//        return contactsList
//    }

    private suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = mapplication.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(Contact(id, name))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    private suspend fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = mapplication.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    private suspend fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = mapplication.contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            null,
            null,
            null)
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId]?.add(email)
                } else {
                    contactsEmailMap[contactId] = arrayListOf(email)
                }
            }
            //contact contains all the emails of a particular contact
            emailCursor.close()
        }
        return contactsEmailMap
    }

    fun fetchContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getPhoneContacts() }
            val contactNumbersAsync = async { getContactNumbers() }


            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()


            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.numbers = numbers
                }

            }
            _contactsLiveData.postValue(contacts)
        }
    }
}