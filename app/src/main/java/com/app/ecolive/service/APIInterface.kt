package com.app.ecolive.service

import com.app.ecolive.pharmacy_module.model.SearchMedicineListData
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


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


    @DELETE("address-delete")
    fun deleteAddress(@QueryMap map: HashMap<String, String>): Call<ResponseBody>

    @POST("social-login")
    fun userSocialLoginAPI(@Body params: RequestBody): Call<ResponseBody>

    @POST("vehicle-categories-list")
    fun vehiclecategorieslistAPI(): Call<ResponseBody>

    @POST("shop-categories-list")
    fun shopcategorieslistAPI(): Call<ResponseBody>

    @POST("rider-vehicle-details")
    fun updateVehicleProfileAPI(@Body requestBody: MultipartBody): Call<ResponseBody>

    @POST("add-shop")
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

    @GET("product-list")
    fun vendorShopProductListAPI(@QueryMap map: HashMap<String, String>): Call<ResponseBody>

    @GET("product-details/{id}")
    fun productDetailApi(@Path("id") id: String): Call<ResponseBody>

    @POST("add-product-to-cart")
    fun addToCart(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("place-retail-order")
    fun PlaceOrderapiEcommerce(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("get-cart-details")
    fun getCart(): Call<ResponseBody>

    @PUT("empty-cart")
    fun removeCart(@Body requestBody: RequestBody): Call<ResponseBody>


    ///pharmacy
    @Multipart
    @POST("create-health-profile")
    fun createHealthProfileApi(
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("ssn") ssn: RequestBody,
        @Part("commonMedication") commonMedication: List<SearchMedicineListData?>,
        @Part images: MultipartBody.Part?,
    ): Call<ResponseBody>

    @POST("create-health-profile")
    fun createHealthProfileApi(@Body requestBody: MultipartBody): Call<ResponseBody>

    @GET("common-medications")
    fun getCommonMedicationApi(): Call<ResponseBody>

    @POST("register-hospital")
    fun creatHospitalApi(@Body requestBody: MultipartBody): Call<ResponseBody>

    @POST("register-hospital-employee")
    fun registerHospitalEmployeeApi(@Body requestBody: RequestBody): Call<ResponseBody>


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

    @POST("get-user-profile/{userID}")
    fun getMyProfile(
        @Path("userID") userID: String,
    ): Call<ResponseBody>


    @POST("update-user-profile")
    fun upadteProfile(@Body requestBody: MultipartBody): Call<ResponseBody>

    @GET("doctor-list")
    fun getDoctorListApi(): Call<ResponseBody>

    // @GET("pharmacy-list/?{lat}&{long}&distance=3000&keyword=&page=1&limit=100")
    @GET("pharmacy-list/")
    fun getPharmacyListApi(
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Query("distance") distance: Int,
        // @Query("keyword") long: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Call<ResponseBody>

    @GET("get-all-readyorders-for-driver/")
    fun getAllReadyOrdersForDriver(
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Query("distance") distance: Int,
        // @Query("keyword") long: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Call<ResponseBody>

    @GET("get-all-order")
    fun getAllOrderApi(): Call<ResponseBody>

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

    @GET("get-medicine")
    fun searchMedicineApi(
        @Query("keyword") keyword: String,
    ): Call<ResponseBody>

    @POST("get-prescription-details")
    fun prescriptionDetailApi(@Body requestBody: RequestBody): Call<ResponseBody>

    /* @POST("start-prescription")
     fun startPrescriptionApi(@Body requestBody: RequestBody): Call<ResponseBody>*/


    @POST("start-prescription")
    fun startPrescriptionApi(
        @Body jsonObject: JsonObject
    ): Call<ResponseBody>

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

    @POST("request-prescription")
    fun requestPrescriptionApi(@Body requestBody: RequestBody): Call<ResponseBody>

    @Multipart
    @POST("request-prescription")
    fun requestPrescriptionApi(
        @Part("doctorId") doctorId: RequestBody,
        @Part("symptomDescription") symptomDescription: RequestBody,
        @Part("symptomDuration") symptomDuration: RequestBody,
        @Part("alreadyMedication") alreadyMedication: RequestBody,
        @Part("recentMedicalHistory") recentMedicalHistory: RequestBody,
        @Part("allergies") allergies: RequestBody,
        @Part("sendPrescriptionToPharmacy") sendPrescriptionToPharmacy: RequestBody,
        @Part("habits") habits: RequestBody,
        @Part("otherRelaventINfotmation") otherRelaventINfotmation: RequestBody,
        @Part("commonMedication") commonMedication: List<SearchMedicineListData?>,
        @Part attachment: MultipartBody.Part?,
        @Part picture: MultipartBody.Part?,
    ): Call<ResponseBody>

    @POST("accept-order")
    fun acceptOrder(@Body requestBody: RequestBody): Call<ResponseBody>

    @PUT("Update-medical-order-by-pharmacy")
    fun updateMedicalOrderByPharmacy(@Body requestBody: RequestBody): Call<ResponseBody>

    //Payment
    @GET("user-list")
    fun getUserList(): Call<ResponseBody>

    @GET("transaction-history")
    fun getTransactionHistory(): Call<ResponseBody>

    @POST("send-money")
    fun sendMoneyApi(@Body params: RequestBody): Call<ResponseBody>

    @POST("add-money-to-wallet")
    fun addMoneyApi(@Body params: RequestBody): Call<ResponseBody>


    @GET("get-wallet")
    fun getWalletApi(): Call<ResponseBody>

    @DELETE("delete-shop-by-shopId/{shopId}")
    fun deleteStore(
        @Path("shopId") shopId: String,
    ): Call<ResponseBody>

    @GET("product-list-by-shopId")
    fun productListByShopID(@QueryMap map: HashMap<String, String>): Call<ResponseBody>

    @GET("order-list-for-user")
    fun orderList(@QueryMap map: HashMap<String, String>): Call<ResponseBody>

    @GET("order-list-for-shop/{id}")
    fun orderListShop(@Path("id") shopId: String,@QueryMap map: HashMap<String, String>): Call<ResponseBody>

    @PUT("toggle-outofstock-status/{id}")
    fun productOutofStockApi(
        @Path("id") shopId: String,
    ): Call<ResponseBody>

    @DELETE("remove-product-by-productId/{id}")
    fun productDeleteApi(
        @Path("id") productId: String,
    ): Call<ResponseBody>

    @GET("geocode/json?")
//    fun geoCodeApi(@QueryMap map: Map<String?, String?>?): Call<GeoCodeResponse>
    fun geoCodeApi(@QueryMap map: HashMap<String?, String?>): Call<ResponseBody>
}