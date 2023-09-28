package com.app.ecolive.viewmodel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.login_module.LoginModel
import com.app.ecolive.login_module.model.BaseModel
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.payment_module.model.UserListModel
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.shop_owner.model.*
import com.app.ecolive.user_module.model.AddressModel
import com.app.ecolive.user_module.model.UserModel
import okhttp3.MultipartBody


import org.json.JSONObject

class PaymentViewModel (activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)
   // lateinit var baseModel: LiveData<ApiSampleResource<BaseModel>>

    private lateinit var userListModel: LiveData<ApiSampleResource<UserListModel>>


    fun getUserList(map: JSONObject): LiveData<ApiSampleResource<UserListModel>> {
        userListModel = webServiceRepository.getUserList(map)
        return userListModel
    }




}