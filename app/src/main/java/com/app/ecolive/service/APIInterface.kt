package com.app.ecolive.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @POST("user-signup")
    fun userSignupAPI(@Body params: RequestBody): Call<ResponseBody>

    @POST("send-mobile-otp")
    fun sendMobileOtpAPI(@Body params: RequestBody): Call<ResponseBody>

    @POST("verify-mobile-otp")
    fun verifyMobileOtpAPI(@Body params: RequestBody): Call<ResponseBody>

    @POST("user-login")
    fun userLoginAPI(@Body params: RequestBody): Call<ResponseBody>

    @POST("address")
    fun addAddres(@Body params: RequestBody): Call<ResponseBody>

    @GET("address")
    fun getAddres(): Call<ResponseBody>


    @POST("address-delete")
    fun deleteAddress(@Body params: RequestBody): Call<ResponseBody>

    @POST("social-login")
    fun userSocialLoginAPI(@Body params: RequestBody): Call<ResponseBody>

    @POST("vehicle-categories-list")
    fun vehiclecategorieslistAPI(): Call<ResponseBody>

    @POST("shop-categories-list")
    fun shopcategorieslistAPI(): Call<ResponseBody>

    @POST("rider-vehicle-details")
    fun updateVehicleProfileAPI(@Body requestBody: MultipartBody): Call<ResponseBody>

    @POST("vendor-shop-details")
    fun shopUploadAPI(@Body requestBody: MultipartBody): Call<ResponseBody>

    @POST("vendor-shop-details")//not use now
    fun shopSignUpAPI(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("introduction-page-list")
    fun introAPI(): Call<ResponseBody>

    @POST("vendor-shop-list")
    fun shopListAPI(): Call<ResponseBody>

    @POST("getStoresAttribute")
    fun attributeListAPI(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("add-product")
    fun addProductListAPI(@Body requestBody: MultipartBody): Call<ResponseBody>

    @POST("vendor-shop-product-list?page=1&limit=30")
    fun vendorShopProductListAPI(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("cart")
    fun addToCart(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("cart")
    fun getCart(): Call<ResponseBody>


    ///pharmacy


    @POST("create-health-profile")
    fun createHealthProfileApi(@Body requestBody: MultipartBody): Call<ResponseBody>

    @GET("common-medications")
    fun getCommonMedicationApi(): Call<ResponseBody>

    @POST("register-hospital")
    fun creatHospitalApi(@Body requestBody: MultipartBody): Call<ResponseBody>

    //Taxi

    @GET("taxi-category-list")
    fun getVehicalApi( ): Call<ResponseBody>

//    @GET("city-list")
//    fun getCityListAPI(): Call<ResponseBody>


//    @FormUrlEncoded
//    @POST("property-list")
//    fun propertyListAPI(@Field("city[]") city: ArrayList<Int>,@Field("status[]") status: ArrayList<String>, @Field("page") page: String , @Field("search_key") srch: String, @Field("filter") filter: String): Call<ResponseBody>


//    @POST("contact-us")
//    fun contactUsAPI(@Body requestBody: MultipartBody): Call<ResponseBody>
}