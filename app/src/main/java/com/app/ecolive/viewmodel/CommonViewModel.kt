package com.app.ecolive.viewmodel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.login_module.LoginModel
import com.app.ecolive.login_module.model.BaseModel
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.shop_owner.model.*
import com.app.ecolive.user_module.model.AddressModel
import okhttp3.MultipartBody


import org.json.JSONObject

class CommonViewModel (activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)
   // lateinit var baseModel: LiveData<ApiSampleResource<BaseModel>>
    private lateinit var addressModel: LiveData<ApiSampleResource<AddressModel>>
    private lateinit var loginModel: LiveData<ApiSampleResource<LoginModel>>
     private lateinit var baseModel: LiveData<ApiSampleResource<BaseModel>>
    private lateinit var vehicalCatgryListModel: LiveData<ApiSampleResource<VehicalCatgryListModel>>
    private lateinit var shopCategryListModel: LiveData<ApiSampleResource<ShopCategryListModel>>
    private lateinit var introModel: LiveData<ApiSampleResource<IntroModel>>
    private lateinit var shopListViewModel: LiveData<ApiSampleResource<ShopListModel>>
    private lateinit var attributeModelViewModel: LiveData<ApiSampleResource<AttributeModel>>
    private lateinit var addProductModelViewModel: LiveData<ApiSampleResource<BaseModel>>
    private lateinit var getProductModelViewModel: LiveData<ApiSampleResource<ProductModel>>
    private lateinit var addtoCartViewModel: LiveData<ApiSampleResource<ProductModel>>
    private lateinit var getCartViewModel: LiveData<ApiSampleResource<GetCartModel>>


    fun userSignUp(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        loginModel = webServiceRepository.userSignUp(map)
        return loginModel
    }

    fun verifyMobileOtp(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.verifyOtp(map)
        return baseModel
    }

    fun sendMobileOtp(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.sendOtpMobile(map)
        return baseModel
    }

    fun userLogin(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        loginModel = webServiceRepository.userLogin(map)
        return loginModel
    }

    fun addAddress(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        addressModel = webServiceRepository.addAddres(map)
        return addressModel
    }

    fun getAddress(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        addressModel = webServiceRepository.getAddress(map)
        return addressModel
    }

    fun deleteAddress(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        addressModel = webServiceRepository.deleteAddress(map)
        return addressModel
    }
    fun userSocialLogin(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        loginModel = webServiceRepository.userSocialLogin(map)
        return loginModel
    }

    fun vehicleCategoriesList(): LiveData<ApiSampleResource<VehicalCatgryListModel>> {
        vehicalCatgryListModel = webServiceRepository.vehicleCategoriesList()
        return vehicalCatgryListModel
    }

    fun shopCategoriesList(): LiveData<ApiSampleResource<ShopCategryListModel>> {
        shopCategryListModel = webServiceRepository.shopCategoriesList()
        return shopCategryListModel
    }
    fun updateVehicleProfile(requestBody: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.updateVehicleProfile(requestBody)
        return baseModel
    }
    fun vendorSignup(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.shopSignup(map)
        return baseModel
    }

    fun introPage(): LiveData<ApiSampleResource<IntroModel>> {
        introModel = webServiceRepository.introPage()
        return introModel
    }
    fun uploadShopSignup(requestBody: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.updateShopSignup(requestBody)
        return baseModel
    }

    fun storeList(): LiveData<ApiSampleResource<ShopListModel>> {
        shopListViewModel = webServiceRepository.shopList()
        return shopListViewModel
    }
    fun attrubuteList(json: JSONObject): LiveData<ApiSampleResource<AttributeModel>> {
        attributeModelViewModel = webServiceRepository.attributeList(json)
        return attributeModelViewModel
    }
    fun addproduct(json: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        addProductModelViewModel = webServiceRepository.addProduct(json)
        return addProductModelViewModel
    }
    fun vendorShopProductList(json: JSONObject): LiveData<ApiSampleResource<ProductModel>> {
        getProductModelViewModel = webServiceRepository.vendorShopProductList(json)
        return getProductModelViewModel
    }
    fun addToCart(json: JSONObject): LiveData<ApiSampleResource<ProductModel>> {
        addtoCartViewModel = webServiceRepository.addToCart(json)
        return addtoCartViewModel
    }
    fun getCart(json: JSONObject): LiveData<ApiSampleResource<GetCartModel>> {
        getCartViewModel = webServiceRepository.getCart(json)
        return getCartViewModel
    }

}