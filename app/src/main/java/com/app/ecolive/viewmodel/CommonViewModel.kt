package com.app.ecolive.viewmodel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.localmodel.OrderListModel
import com.app.ecolive.localmodel.StatusUpdateModel
import com.app.ecolive.login_module.model.LoginModel
import com.app.ecolive.login_module.model.BaseModel
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.shop_owner.model.*
import com.app.ecolive.user_module.model.AddressModel
import com.emizen.chomp.pojo.GeoCodeResponse
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
    private lateinit var deleteStoreViewModel: LiveData<ApiSampleResource<ShopListModel>>
    private lateinit var attributeModelViewModel: LiveData<ApiSampleResource<AttributeModel>>
    private lateinit var addProductModelViewModel: LiveData<ApiSampleResource<BaseModel>>
    private lateinit var getProductModelViewModel: LiveData<ApiSampleResource<ProductModel>>
    private lateinit var productDetailModel: LiveData<ApiSampleResource<ProductDetailModel>>
    private lateinit var getProductListModelViewModel: LiveData<ApiSampleResource<ProductListModel>>
    private lateinit var getMyOrderListViewModel: LiveData<ApiSampleResource<OrderListModel>>
    private lateinit var orderStatusViewModel: LiveData<ApiSampleResource<StatusUpdateModel>>
    private lateinit var productOutofStockViewModel: LiveData<ApiSampleResource<ProductOutofStockModel>>
    private lateinit var addtoCartViewModel: LiveData<ApiSampleResource<AddToCartModel>>
    private lateinit var placeOrderViewModel: LiveData<ApiSampleResource<PlaceOrderModel>>
    private lateinit var getCartViewModel: LiveData<ApiSampleResource<GetCartModel>>
    private lateinit var reverseGeoCodeApi: LiveData<ApiSampleResource<GeoCodeResponse>>


    fun userSignUp(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        loginModel = webServiceRepository.userSignUp(map)
        return loginModel
    }

    fun verifyMobileOtp(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.verifyOtp(map)
        return baseModel
    }

    fun getMyProfile(id: String): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.getMyProfile(id)
        return baseModel
    }
    fun DeactivateApi(id: String): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.DeactivateApi(id)
        return baseModel
    }

    fun logoutApi(id: String): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.logoutApi(id)
        return baseModel
    }
    fun updateProfileApi(body: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        baseModel = webServiceRepository.updateProfileApi(body)
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

    fun deleteAddress(map: HashMap<String,String>): LiveData<ApiSampleResource<AddressModel>> {
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

    fun deleteStore(id: String): LiveData<ApiSampleResource<ShopListModel>> {
        deleteStoreViewModel = webServiceRepository.deleteStore(id)
        return deleteStoreViewModel
    }
    fun attrubuteList(json: JSONObject): LiveData<ApiSampleResource<AttributeModel>> {
        attributeModelViewModel = webServiceRepository.attributeList(json)
        return attributeModelViewModel
    }
    fun addproduct(json: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        addProductModelViewModel = webServiceRepository.addProduct(json)
        return addProductModelViewModel
    }
    fun productDetailApi(id: String): LiveData<ApiSampleResource<ProductDetailModel>> {
        productDetailModel = webServiceRepository.productDetailApi(id)
        return productDetailModel
    }

    fun vendorShopProductList(map: HashMap<String, String>): LiveData<ApiSampleResource<ProductModel>> {
        getProductModelViewModel = webServiceRepository.vendorShopProductList(map)
        return getProductModelViewModel
    }

    fun shopProductListByid(map: HashMap<String, String>): LiveData<ApiSampleResource<ProductListModel>> {
        getProductListModelViewModel = webServiceRepository.shopProductListByid(map)
        return getProductListModelViewModel
    }

    fun orderList(map: HashMap<String, String>): LiveData<ApiSampleResource<OrderListModel>> {
        getMyOrderListViewModel = webServiceRepository.orderList(map)
        return getMyOrderListViewModel
    }
    fun orderListShop(id:String,map: HashMap<String, String>): LiveData<ApiSampleResource<OrderListModel>> {
        getMyOrderListViewModel = webServiceRepository.orderListShop(id,map)
        return getMyOrderListViewModel
    }

    fun updateOrderStatus(id:String,map: HashMap<String, String>): LiveData<ApiSampleResource<StatusUpdateModel>> {
        orderStatusViewModel = webServiceRepository.updateOrderStatus(id,map)
        return orderStatusViewModel
    }
    fun productOutofStockApi(id: String): LiveData<ApiSampleResource<ProductOutofStockModel>> {
        productOutofStockViewModel = webServiceRepository.productOutofStockApi(id)
        return productOutofStockViewModel
    }
    fun productDeleteApi(id: String): LiveData<ApiSampleResource<ProductOutofStockModel>> {
        productOutofStockViewModel = webServiceRepository.productDeleteApi(id)
        return productOutofStockViewModel
    }
    fun addToCart(json: JSONObject): LiveData<ApiSampleResource<AddToCartModel>> {
        addtoCartViewModel = webServiceRepository.addToCart(json)
        return addtoCartViewModel
    }
    fun getCart(json: JSONObject): LiveData<ApiSampleResource<GetCartModel>> {
        getCartViewModel = webServiceRepository.getCart(json)
        return getCartViewModel
    }

    fun removeCart(json: JSONObject): LiveData<ApiSampleResource<GetCartModel>> {
        getCartViewModel = webServiceRepository.removeCart(json)
        return getCartViewModel
    }

    fun reverseApi(map: HashMap<String?, String?>): LiveData<ApiSampleResource<GeoCodeResponse>> {
        reverseGeoCodeApi = webServiceRepository.reverseApi(map)
        return reverseGeoCodeApi
    }

    fun PlaceOrderapi(json: JSONObject): LiveData<ApiSampleResource<PlaceOrderModel>> {
        placeOrderViewModel = webServiceRepository.PlaceOrderapiEcommerce(json)
        return placeOrderViewModel
    }
}