package com.app.ecolive.viewmodel

import android.app.Activity
import android.provider.ContactsContract
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ecolive.login_module.model.BaseModel
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.payment_module.model.Contact
import com.app.ecolive.payment_module.model.TransactionHistoryModel
import com.app.ecolive.payment_module.model.UserListModel
import com.app.ecolive.payment_module.model.WalletModel
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.shop_owner.model.*
import com.app.ecolive.user_module.model.AddressModel
 import okhttp3.MultipartBody



import org.json.JSONObject

class PaymentViewModel (activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)
   // lateinit var baseModel: LiveData<ApiSampleResource<BaseModel>>


    private lateinit var userListModel: LiveData<ApiSampleResource<UserListModel>>
    private lateinit var getTransacionHistoryModel: LiveData<ApiSampleResource<TransactionHistoryModel>>
    private lateinit var getWalletAmount: LiveData<ApiSampleResource<WalletModel>>
    private lateinit var sendMoney: LiveData<ApiSampleResource<WalletModel>>
    private lateinit var addMoney: LiveData<ApiSampleResource<WalletModel>>


    fun getUserList(map: JSONObject): LiveData<ApiSampleResource<UserListModel>> {
        userListModel = webServiceRepository.getUserList(map)
        return userListModel
    }

    fun getTransactionHistory(map: JSONObject): LiveData<ApiSampleResource<TransactionHistoryModel>> {
        getTransacionHistoryModel = webServiceRepository.getTransactionHistory(map)
        return getTransacionHistoryModel
    }
    fun getWalletApi(map: JSONObject): LiveData<ApiSampleResource<WalletModel>> {
        getWalletAmount = webServiceRepository.getWalletApi(map)
        return getWalletAmount
    }

    fun sendMoneyApi(map: JSONObject): LiveData<ApiSampleResource<WalletModel>> {
        sendMoney = webServiceRepository.sendMoneyApi(map)
        return sendMoney
    }

    fun addMoneyApi(map: JSONObject): LiveData<ApiSampleResource<WalletModel>> {
        addMoney = webServiceRepository.addMoneyApi(map)
        return addMoney
    }

}