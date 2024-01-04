package com.app.ecolive.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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

    @POST("register-hospital-employee")
    fun registerHospitalEmployeeApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("request-prescription")
    fun requestPrescriptionApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("create-order")
    fun placeOrderApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("taxi-category-list")
    fun getVehicalApi(): Call<ResponseBody>

    @POST("request-taxi")
    fun confirmTaxiApi(@Body params: RequestBody): Call<ResponseBody>

    @POST("schedule-ride")
    fun scheduleRideApi(@Body params: RequestBody): Call<ResponseBody>

    @GET("bokings-request")
    fun getTaxiBookingRequestListApi(): Call<ResponseBody>

    @GET("get-doctor-profile")
    fun getDoctorProfileApi(
    ): Call<ResponseBody>

    @GET("all-health-profile")
    fun getHealthProfile(
    ): Call<ResponseBody>

    @GET("bokings-request/driver/{driverID}")
    fun riderOrderListApi(
        @Path("driverID") driverID: String,
    ): Call<ResponseBody>

    @GET("doctor-list")
    fun getDoctorListApi(): Call<ResponseBody>

    @GET("get-prescription-by-user")
    fun userPrescriptionListApi(): Call<ResponseBody>

    @POST("change-taxi-availability-status")
    fun checkDemandRideApi(@Body params: RequestBody): Call<ResponseBody>

    @POST("accept-booking-request")
    fun acceptBookingRequestRideApi(@Body params: RequestBody): Call<ResponseBody>

    @POST("start-booking-request")
    fun startRideRequestRideApi(@Body params: RequestBody): Call<ResponseBody>

    @POST("decline-booking-request")
    fun declineBookingRequestRideApi(@Body params: RequestBody): Call<ResponseBody>

    @POST("complete-booking-request")
    fun completeBookingRequestRideApi(@Body params: RequestBody): Call<ResponseBody>

    @GET("rider-profile")
    fun getRiderProfileApi(): Call<ResponseBody>
    @GET("search-medicines")
    fun searchMedicineApi(): Call<ResponseBody>

    @POST("get-prescription-details")
    fun prescriptionDetailApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("start-prescription")
    fun startPrescriptionApi(@Body requestBody: RequestBody): Call<ResponseBody>
    @POST("cancle-precription")
    fun cancelPrescriptionApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("add-or-update-pharmacy")
    fun createAndUpdatePharmacyApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("pharmacy-profile")
    fun getPharmacyProfile(): Call<ResponseBody>

    @POST("add-medicine")
    fun addMedicine(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("get-all-medicine/{userId}")
    fun getAllMedicine(
        @Path("userId") userId: String,
    ): Call<ResponseBody>

    @PUT("update-pharmacy-status/{pharmacyId}")
    fun updatePharmacyStatus(
        @Path("pharmacyId") pharmacyId: String,
    ): Call<ResponseBody>

    @POST("get-prescription-request-for-doctor")
    fun getPrescriptionRequestForDoctor(@Body requestBody: RequestBody): Call<ResponseBody>

}