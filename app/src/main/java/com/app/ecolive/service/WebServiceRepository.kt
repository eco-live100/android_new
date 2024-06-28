package com.app.ecolive.service

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ecolive.R
import com.app.ecolive.localmodel.OrderListModel
import com.app.ecolive.localmodel.StatusUpdateModel
import com.app.ecolive.login_module.model.BaseModel
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.login_module.model.LoginModel
import com.app.ecolive.payment_module.model.TransactionHistoryModel
import com.app.ecolive.payment_module.model.UserListModel
import com.app.ecolive.payment_module.model.WalletModel
import com.app.ecolive.pharmacy_module.model.AddMedicineModel
import com.app.ecolive.pharmacy_module.model.AllOrderModel
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import com.app.ecolive.pharmacy_module.model.CreateHealthProfileModel
import com.app.ecolive.pharmacy_module.model.DoctorListModel
import com.app.ecolive.pharmacy_module.model.DoctorProfileModel
import com.app.ecolive.pharmacy_module.model.GetAllReadyOrdersForDriver
import com.app.ecolive.pharmacy_module.model.HealthProfileModel
import com.app.ecolive.pharmacy_module.model.MedicineListModel
import com.app.ecolive.pharmacy_module.model.PharmacyListModel
import com.app.ecolive.pharmacy_module.model.PharmacyProfileModel
import com.app.ecolive.pharmacy_module.model.PrescriptionListModel
import com.app.ecolive.pharmacy_module.model.PrescriptionRequestData
import com.app.ecolive.pharmacy_module.model.RequestPrescriptionModel
import com.app.ecolive.pharmacy_module.model.SearchMedicineList
import com.app.ecolive.pharmacy_module.model.SearchMedicineListData
import com.app.ecolive.pharmacy_module.model.UserPrescriptionModel
import com.app.ecolive.rider_module.model.RiderOrderModel
import com.app.ecolive.rider_module.model.RiderProfileModel
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.shop_owner.model.AddToCartModel
import com.app.ecolive.shop_owner.model.AttributeModel
import com.app.ecolive.shop_owner.model.GetCartModel
import com.app.ecolive.shop_owner.model.PlaceOrderModel
import com.app.ecolive.shop_owner.model.ProductDetailModel
import com.app.ecolive.shop_owner.model.ProductListModel
import com.app.ecolive.shop_owner.model.ProductModel
import com.app.ecolive.shop_owner.model.ProductOutofStockModel
import com.app.ecolive.shop_owner.model.ShopCategryListModel
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.taximodule.model.CommonModel
import com.app.ecolive.taximodule.model.ConfirmTaxiModel
import com.app.ecolive.taximodule.model.ScheduleRideModel
import com.app.ecolive.taximodule.model.TaxiBookingRequestList
import com.app.ecolive.taximodule.model.VehicleModel
import com.app.ecolive.user_module.model.AddressModel
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.AppConstant.INTERNAL_ERROR
import com.app.ecolive.utils.AppConstant.NO_INTERNET
import com.app.ecolive.utils.AppConstant.PARSING_ERROR
import com.emizen.chomp.pojo.GeoCodeResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class WebServiceRepository(application: Activity) {

    private var apiInterfaceHeader: APIInterface = APIClient.makeRetrofitServiceHeader()
    private var apiInterfaceHeader2: APIInterface = APIClient.makeRetrofitServiceHeader2()
    private var networkHelper: NetworkHelper = NetworkHelper(application)
    var application = application

    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun userSignUp(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userSignupAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun sendOtpMobile(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.sendMobileOtpAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun verifyOtp(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.verifyMobileOtpAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun userLogin(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val loginResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userLoginAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
                                loginResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                loginResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            loginResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            loginResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            loginResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        loginResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        loginResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else loginResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return loginResponseModel
    }

    fun getMyProfile(id: String): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getMyProfile(id)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 404,408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun logoutApi(id: String): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.logoutApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 404,408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun DeactivateApi(id: String): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.DeactivateApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 404,408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun updateProfileApi(body: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.upadteProfile(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 404,408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }
    fun userSocialLogin(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userSocialLoginAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun vehicleCategoriesList(): LiveData<ApiSampleResource<VehicalCatgryListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<VehicalCatgryListModel>>()
        if (networkHelper.isNetworkConnected()) {
            //  val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.vehiclecategorieslistAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<VehicalCatgryListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun shopCategoriesList(): LiveData<ApiSampleResource<ShopCategryListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ShopCategryListModel>>()
        if (networkHelper.isNetworkConnected()) {
            //  val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopcategorieslistAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ShopCategryListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun updateVehicleProfile(requestBody: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.updateVehicleProfileAPI(requestBody)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)

                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET,
                application.resources.getString(R.string.No_Internet),
                null
            )
        )
        return venueListResponseModel
    }

    fun updateShopSignup(requestBody: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopUploadAPI(requestBody)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)

                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET,
                application.resources.getString(R.string.No_Internet),
                null
            )
        )
        return venueListResponseModel
    }

    fun shopSignup(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopSignUpAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        200, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun introPage(): LiveData<ApiSampleResource<IntroModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<IntroModel>>()
        if (networkHelper.isNetworkConnected()) {
            //    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.introAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<IntroModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun shopList(): LiveData<ApiSampleResource<ShopListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ShopListModel>>()
        if (networkHelper.isNetworkConnected()) {
            //    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopListAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ShopListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun deleteStore(id: String): LiveData<ApiSampleResource<ShopListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ShopListModel>>()
        if (networkHelper.isNetworkConnected()) {
            //    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.deleteStore(id)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ShopListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun attributeList(json: JSONObject): LiveData<ApiSampleResource<AttributeModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<AttributeModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.attributeListAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AttributeModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun addProduct(body: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addProductListAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<BaseModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun vendorShopProductList(map: HashMap<String, String>): LiveData<ApiSampleResource<ProductModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductModel>>()
        if (networkHelper.isNetworkConnected()) {

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.vendorShopProductListAPI(map)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun productDetailApi(id: String): LiveData<ApiSampleResource<ProductDetailModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductDetailModel>>()
        if (networkHelper.isNetworkConnected()) {
           /* val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.productDetailApi(id)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductDetailModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun shopProductListByid(map: HashMap<String, String>): LiveData<ApiSampleResource<ProductListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductListModel>>()
        if (networkHelper.isNetworkConnected()) {
          /*  val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.productListByShopID(map)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun orderList(map: HashMap<String, String>): LiveData<ApiSampleResource<OrderListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<OrderListModel>>()
        if (networkHelper.isNetworkConnected()) {
            /*  val body = RequestBody.create(
                  "application/json; charset=utf-8".toMediaTypeOrNull(),
                  json.toString()
              )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.orderList(map)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<OrderListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun orderListShop(id:String,map: HashMap<String, String>): LiveData<ApiSampleResource<OrderListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<OrderListModel>>()
        if (networkHelper.isNetworkConnected()) {
            /*  val body = RequestBody.create(
                  "application/json; charset=utf-8".toMediaTypeOrNull(),
                  json.toString()
              )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.orderListShop(id,map)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<OrderListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun updateOrderStatus(id:String,map: HashMap<String, String>): LiveData<ApiSampleResource<StatusUpdateModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<StatusUpdateModel>>()
        if (networkHelper.isNetworkConnected()) {
            /*  val body = RequestBody.create(
                  "application/json; charset=utf-8".toMediaTypeOrNull(),
                  json.toString()
              )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.updateOrderStatus(id,map)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<StatusUpdateModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun productOutofStockApi(id: String): LiveData<ApiSampleResource<ProductOutofStockModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductOutofStockModel>>()
        if (networkHelper.isNetworkConnected()) {
            /*  val body = RequestBody.create(
                  "application/json; charset=utf-8".toMediaTypeOrNull(),
                  json.toString()
              )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.productOutofStockApi(id)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductOutofStockModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun productDeleteApi(id: String): LiveData<ApiSampleResource<ProductOutofStockModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductOutofStockModel>>()
        if (networkHelper.isNetworkConnected()) {
            /*  val body = RequestBody.create(
                  "application/json; charset=utf-8".toMediaTypeOrNull(),
                  json.toString()
              )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.productDeleteApi(id)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductOutofStockModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    /* fun doForgot(map: HashMap<String, String>): LiveData<ApiSampleResource<BaseModel>> {
         val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
         if (networkHelper.isNetworkConnected()) {
             val responseBody: Call<ResponseBody> = apiInterfaceHeader.forgotAPI(map)
             responseBody.enqueue(object : Callback<ResponseBody> {
                 override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                     when (response.code()) {
                         200 -> {
                             val data = response.body()?.string()!!
                             try {
                                 val dataResponse = fromJson<BaseModel>(data)
                                 venueListResponseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                             } catch (ex: Exception) {
                                 ex.printStackTrace()
                                 venueListResponseModel.postValue(ApiSampleResource.error(
                                     PARSING_ERROR,
                                     application.resources.getString(R.string.Parsing_Problem),
                                     null))
                             }
                         }
                         204->{
                             venueListResponseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                         }
                         201,205,400,401,408,409-> {
                             val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                             var vv=jsonObj.getString("message")
                             venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                         }
                         500->{
                             venueListResponseModel.postValue( ApiSampleResource.error(
                                 response.code(),
                                 application.resources.getString(R.string.Internal_server_error),
                                 null))

                         }
                     }
                 }

                 override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                     if (t is IOException) {
                         venueListResponseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                     } else {
                         venueListResponseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                     }
                 }

             })
         } else venueListResponseModel.postValue(ApiSampleResource.error(AppConstant.NO_INTERNET, application.resources.getString(
             R.string.No_Internet), null))
         return venueListResponseModel
     }*/

    /* fun dashBoard(jbj:JSONObject): LiveData<ApiSampleResource<DashboardModel>> {
         val venueListResponseModel = MutableLiveData<ApiSampleResource<DashboardModel>>()
         val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jbj.toString())
         if (networkHelper.isNetworkConnected()) {
             val responseBody: Call<ResponseBody> = apiInterfaceHeader.dashboardAPI(body)
             responseBody.enqueue(object : Callback<ResponseBody> {
                 override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                     when (response.code()) {
                         200 -> {
                             val data = response.body()?.string()!!
                             try {
                                 val dataResponse = fromJson<DashboardModel>(data)

                                 venueListResponseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                             } catch (ex: Exception) {
                                 ex.printStackTrace()
                                 venueListResponseModel.postValue(ApiSampleResource.error(
                                     PARSING_ERROR,
                                     application.resources.getString(R.string.Parsing_Problem),
                                     null))
                             }
                         }
                         204->{
                             venueListResponseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                         }
                         201,205,400,401,408,409-> {
                             val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                             var vv=jsonObj.getString("message")
                             venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                         }
                         500->{
                             venueListResponseModel.postValue( ApiSampleResource.error(
                                 response.code(),
                                 application.resources.getString(R.string.Internal_server_error),
                                 null))

                         }
                     }
                 }

                 override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                     if (t is IOException) {
                         venueListResponseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                     } else {
                         venueListResponseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                     }
                 }

             })
         } else venueListResponseModel.postValue(ApiSampleResource.error(NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
         return venueListResponseModel
     }*/

    fun addAddres(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<AddressModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addAddres(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddressModel>(data)
                                responseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseModel
    }

    fun getAddress(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<AddressModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getAddres()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddressModel>(data)
                                responseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseModel
    }

    fun deleteAddress(map: HashMap<String,String>): LiveData<ApiSampleResource<AddressModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<AddressModel>>()
        if (networkHelper.isNetworkConnected()) {
          /*  val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.deleteAddress(map)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddressModel>(data)
                                responseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getString("message")
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                        }

                        500 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseModel
    }

    fun addToCart(json: JSONObject): LiveData<ApiSampleResource<AddToCartModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<AddToCartModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addToCart(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddToCartModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun getCart(json: JSONObject): LiveData<ApiSampleResource<GetCartModel>> {
        val ResponseModel = MutableLiveData<ApiSampleResource<GetCartModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getCart()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<GetCartModel>(data)
                                ResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                ResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            ResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                ResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            ResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        ResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        ResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else ResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return ResponseModel
    }

    fun removeCart(id: JSONObject): LiveData<ApiSampleResource<GetCartModel>> {
        val ResponseModel = MutableLiveData<ApiSampleResource<GetCartModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                id.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.removeCart(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<GetCartModel>(data)
                                ResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                ResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            ResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                ResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            ResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        ResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        ResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else ResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return ResponseModel
    }

    fun PlaceOrderapiEcommerce(json: JSONObject): LiveData<ApiSampleResource<PlaceOrderModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<PlaceOrderModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.PlaceOrderapiEcommerce(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PlaceOrderModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        201, 205, 400, 404, 401, 408, 409 -> {
                            try {
                                val jsonObj =
                                    JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                            } catch (e: Exception) {
                            }

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun createHealthProfileApi(
        name: RequestBody,
        ssn: RequestBody,
        address: RequestBody,
        images: MultipartBody.Part?,
        commonMedication: List<SearchMedicineListData?>
    ): LiveData<ApiSampleResource<CreateHealthProfileModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CreateHealthProfileModel>>()
        if (networkHelper.isNetworkConnected()) {

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.createHealthProfileApi(
                name = name,
                ssn = ssn,
                address = address,
                images = images,
                commonMedication = commonMedication,
            )
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CreateHealthProfileModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun createHealthProfileApi(body: MultipartBody): LiveData<ApiSampleResource<CreateHealthProfileModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CreateHealthProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.createHealthProfileApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CreateHealthProfileModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


    fun getCommonMedicationApi(): LiveData<ApiSampleResource<CommonMedicationModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CommonMedicationModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getCommonMedicationApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonMedicationModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun registerHospitalEmployeeApi(body: MultipartBody): LiveData<ApiSampleResource<DoctorProfileModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<DoctorProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.registerHospitalEmployeeApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<DoctorProfileModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

/*
    fun requestPrescriptionApi(body: MultipartBody): LiveData<ApiSampleResource<RequestPrescriptionModel>> {
            val responseModel = MutableLiveData<ApiSampleResource<RequestPrescriptionModel>>()
            if (networkHelper.isNetworkConnected()) {
    //            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
                val responseBody: Call<ResponseBody> = apiInterfaceHeader.requestPrescriptionApi(body)
                responseBody.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        when (response.code()) {
                            200, 201 -> {
                                val data = response.body()?.string()!!
                                try {
                                    val dataResponse = fromJson<RequestPrescriptionModel>(data)
                                    responseModel.postValue(
                                        ApiSampleResource.success(
                                            response.code(),
                                            response.message(),
                                            dataResponse
                                        )
                                    )
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    responseModel.postValue(
                                        ApiSampleResource.error(
                                            PARSING_ERROR,
                                            application.resources.getString(R.string.Parsing_Problem),
                                            null
                                        )
                                    )
                                }
                            }

                            204 -> {
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        application.resources.getString(R.string.No_data_found),
                                        null
                                    )
                                )
                            }

                            205, 400, 404, 401, 408, 409 -> {
                                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                                var vv = jsonObj.getJSONObject("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        jsonObj.getString("message"),
                                        null
                                    )
                                )
                                // responseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                            }

                            500 -> {
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        response.code(),
                                        application.resources.getString(R.string.Internal_server_error),
                                        null
                                    )
                                )

                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        if (t is IOException) {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    INTERNAL_ERROR,
                                    application.resources.getString(R.string.Network_Failure),
                                    null
                                )
                            )
                        } else {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Something_went_wrong),
                                    null
                                )
                            )
                        }
                    }

                })
            } else responseModel.postValue(
                ApiSampleResource.error(
                    NO_INTERNET, application.resources.getString(R.string.No_Internet), null
                )
            )
            return responseModel
        }
*/


    fun requestPrescriptionApi(
        doctorId: RequestBody,
        symptomDescription: RequestBody,
        symptomDuration: RequestBody,
        alreadyMedication: RequestBody,
        recentMedicalHistory: RequestBody,
        allergies: RequestBody,
        sendPrescriptionToPharmacy: RequestBody,
        habits: RequestBody,
        otherRelaventINfotmation: RequestBody,
        commonMedication: List<SearchMedicineListData?>,
        attachment: MultipartBody.Part?,
        picture: MultipartBody.Part?,
    ): LiveData<ApiSampleResource<RequestPrescriptionModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<RequestPrescriptionModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.requestPrescriptionApi(
                doctorId = doctorId,
                symptomDescription = symptomDescription,
                symptomDuration = symptomDuration,
                alreadyMedication = alreadyMedication,
                recentMedicalHistory = recentMedicalHistory,
                allergies = allergies,
                sendPrescriptionToPharmacy = sendPrescriptionToPharmacy,
                habits = habits,
                otherRelaventINfotmation = otherRelaventINfotmation,
                commonMedication = commonMedication,
                attachment = attachment,
                picture = picture
            )
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<RequestPrescriptionModel>(data)
                                responseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // responseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseModel
    }

    fun placeOrderApi(body: MultipartBody): LiveData<ApiSampleResource<CommonModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.placeOrderApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                responseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401,405, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // responseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseModel
    }
    ///Taxi

    fun getVehicalApi(body: JSONObject): LiveData<ApiSampleResource<VehicleModel>> {
        val responseData = MutableLiveData<ApiSampleResource<VehicleModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getVehicalApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<VehicleModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }


    fun confirmTaxiApi(map: JSONObject): LiveData<ApiSampleResource<ConfirmTaxiModel>> {
        val confirmTaxiResponseModel = MutableLiveData<ApiSampleResource<ConfirmTaxiModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.confirmTaxiApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ConfirmTaxiModel>(data)
                                confirmTaxiResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                confirmTaxiResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            confirmTaxiResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            confirmTaxiResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            confirmTaxiResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        confirmTaxiResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        confirmTaxiResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else confirmTaxiResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return confirmTaxiResponseModel
    }

    fun scheduleRideApi(map: JSONObject): LiveData<ApiSampleResource<ScheduleRideModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<ScheduleRideModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.scheduleRideApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ScheduleRideModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun getTaxiBookingRequestListApi(body: JSONObject): LiveData<ApiSampleResource<TaxiBookingRequestList>> {
        val responseData = MutableLiveData<ApiSampleResource<TaxiBookingRequestList>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getTaxiBookingRequestListApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<TaxiBookingRequestList>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun riderOrderListApi(driverID: String): LiveData<ApiSampleResource<RiderOrderModel>> {
        val responseData = MutableLiveData<ApiSampleResource<RiderOrderModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.riderOrderListApi(driverID = driverID)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<RiderOrderModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun getDoctorProfileApi(/*userId: String,professionType:String*/): LiveData<ApiSampleResource<DoctorProfileModel>> {
        val responseData = MutableLiveData<ApiSampleResource<DoctorProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.getDoctorProfileApi(/*userId = userId, professionType = professionType*/)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<DoctorProfileModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
//                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun getHealthProfile(): LiveData<ApiSampleResource<HealthProfileModel>> {
        val responseData = MutableLiveData<ApiSampleResource<HealthProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getHealthProfile()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<HealthProfileModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun getDoctorListApi(): LiveData<ApiSampleResource<DoctorListModel>> {
        val responseData = MutableLiveData<ApiSampleResource<DoctorListModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getDoctorListApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<DoctorListModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }
    fun getAllOrderApi(): LiveData<ApiSampleResource<AllOrderModel>> {
        val responseData = MutableLiveData<ApiSampleResource<AllOrderModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getAllOrderApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AllOrderModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun getPharmacyListApi(
        lat: Double,
        long: Double,
        distance: Int,
        //keyword: String,
        page: Int,
        limit: Int,
    ): LiveData<ApiSampleResource<PharmacyListModel>> {
        val responseData = MutableLiveData<ApiSampleResource<PharmacyListModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.getPharmacyListApi(lat = lat, long = long, distance = distance, page = page, limit = limit)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PharmacyListModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }
    fun getAllReadyOrdersForDriver(
        lat: Double,
        long: Double,
        distance: Int,
        //keyword: String,
        page: Int,
        limit: Int,
    ): LiveData<ApiSampleResource<GetAllReadyOrdersForDriver>> {
        val responseData = MutableLiveData<ApiSampleResource<GetAllReadyOrdersForDriver>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.getAllReadyOrdersForDriver(lat = lat, long = long, distance = distance, page = page, limit = limit)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<GetAllReadyOrdersForDriver>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun userPrescriptionListApi(): LiveData<ApiSampleResource<UserPrescriptionModel>> {
        val responseData = MutableLiveData<ApiSampleResource<UserPrescriptionModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userPrescriptionListApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<UserPrescriptionModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun checkDemandRideApi(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.checkDemandRideApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun getRiderProfileApi(map: JSONObject): LiveData<ApiSampleResource<RiderProfileModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<RiderProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
            //val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getRiderProfileApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<RiderProfileModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun acceptBookingRequestRideApi(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.acceptBookingRequestRideApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }
    fun acceptOrder(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.acceptOrder(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }
    fun updateMedicalOrderByPharmacy(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.updateMedicalOrderByPharmacy(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun startRideRequestRideApi(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.startRideRequestRideApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun declineBookingRequestRideApi(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.declineBookingRequestRideApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun completeBookingRequestRideApi(map: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val scheduleRideResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                map.toString()
            )
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.completeBookingRequestRideApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                scheduleRideResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message").getString("msg")
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    vv,
                                    null
                                )
                            )
                        }

                        500 -> {
                            scheduleRideResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        scheduleRideResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else scheduleRideResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return scheduleRideResponseModel
    }

    fun searchMedicineApi(search: String): LiveData<ApiSampleResource<SearchMedicineList>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<SearchMedicineList>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.searchMedicineApi(keyword = search)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<SearchMedicineList>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun prescriptionDetailApi(json: JSONObject): LiveData<ApiSampleResource<PrescriptionRequestData>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<PrescriptionRequestData>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )

            val responseBody: Call<ResponseBody> = apiInterfaceHeader.prescriptionDetailApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PrescriptionRequestData>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun startPrescriptionApi(json: JsonObject): LiveData<ApiSampleResource<CommonModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            /*val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )*/
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.startPrescriptionApi(json)

            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun cancelPrescriptionApi(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CommonModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.cancelPrescriptionApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun createAndUpdatePharmacyApi(body: MultipartBody): LiveData<ApiSampleResource<PharmacyProfileModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<PharmacyProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.createAndUpdatePharmacyApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PharmacyProfileModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun getPharmacyProfile(): LiveData<ApiSampleResource<PharmacyProfileModel>> {
        val responseData = MutableLiveData<ApiSampleResource<PharmacyProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getPharmacyProfile()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PharmacyProfileModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409, 504 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            //var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun addMedicineApi(body: MultipartBody): LiveData<ApiSampleResource<AddMedicineModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<AddMedicineModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addMedicine(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddMedicineModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }

    fun getMedicineListApi(userId: String): LiveData<ApiSampleResource<MedicineListModel>> {
        val responseData = MutableLiveData<ApiSampleResource<MedicineListModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.getAllMedicine(userId = userId)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<MedicineListModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun updatePharmacyStatus(pharmacyId: String): LiveData<ApiSampleResource<PharmacyProfileModel>> {
        val responseData = MutableLiveData<ApiSampleResource<PharmacyProfileModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.updatePharmacyStatus(pharmacyId = pharmacyId)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        201, 200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PharmacyProfileModel>(data)
                                responseData.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            responseData.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        responseData.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else responseData.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return responseData
    }

    fun getPrescriptionRequestForDoctorApi(json: JSONObject): LiveData<ApiSampleResource<PrescriptionListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<PrescriptionListModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val responseBody: Call<ResponseBody> =
                apiInterfaceHeader.getPrescriptionRequestForDoctor(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when (response.code()) {
                        200, 201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<PrescriptionListModel>(data)
                                venueListResponseModel.postValue(
                                    ApiSampleResource.success(
                                        response.code(),
                                        response.message(),
                                        dataResponse
                                    )
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                venueListResponseModel.postValue(
                                    ApiSampleResource.error(
                                        PARSING_ERROR,
                                        application.resources.getString(R.string.Parsing_Problem),
                                        null
                                    )
                                )
                            }
                        }

                        204 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.No_data_found),
                                    null
                                )
                            )
                        }

                        205, 400, 404, 401, 408, 409 -> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv = jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    jsonObj.getString("message"),
                                    null
                                )
                            )
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }

                        500 -> {
                            venueListResponseModel.postValue(
                                ApiSampleResource.error(
                                    response.code(),
                                    application.resources.getString(R.string.Internal_server_error),
                                    null
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                INTERNAL_ERROR,
                                application.resources.getString(R.string.Network_Failure),
                                null
                            )
                        )
                    } else {
                        venueListResponseModel.postValue(
                            ApiSampleResource.error(
                                PARSING_ERROR,
                                application.resources.getString(R.string.Something_went_wrong),
                                null
                            )
                        )
                    }
                }

            })
        } else venueListResponseModel.postValue(
            ApiSampleResource.error(
                NO_INTERNET, application.resources.getString(R.string.No_Internet), null
            )
        )
        return venueListResponseModel
    }


     //  Payment
    fun getUserList(body: JSONObject): LiveData<ApiSampleResource<UserListModel>> {
        val responseData = MutableLiveData<ApiSampleResource<UserListModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getUserList()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<UserListModel>(data)
                                responseData.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseData.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            responseData.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseData.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseData.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseData
    }

    fun getTransactionHistory(body: JSONObject): LiveData<ApiSampleResource<TransactionHistoryModel>> {
        val responseData = MutableLiveData<ApiSampleResource<TransactionHistoryModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getTransactionHistory()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<TransactionHistoryModel>(data)
                                responseData.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseData.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            responseData.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseData.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseData.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseData
    }

    fun getWalletApi(body: JSONObject): LiveData<ApiSampleResource<WalletModel>> {
        val responseData = MutableLiveData<ApiSampleResource<WalletModel>>()
        if (networkHelper.isNetworkConnected()) {
        //    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getWalletApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<WalletModel>(data)
                                responseData.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseData.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            responseData.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseData.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseData.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseData
    }

    fun sendMoneyApi(body: JSONObject): LiveData<ApiSampleResource<WalletModel>> {
        val responseData = MutableLiveData<ApiSampleResource<WalletModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.sendMoneyApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<WalletModel>(data)
                                responseData.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseData.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            responseData.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseData.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseData.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseData
    }

    fun addMoneyApi(body: JSONObject): LiveData<ApiSampleResource<WalletModel>> {
        val responseData = MutableLiveData<ApiSampleResource<WalletModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addMoneyApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<WalletModel>(data)
                                responseData.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseData.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            responseData.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseData.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseData.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseData
    }

    fun reverseApi(body: HashMap<String?, String?>): LiveData<ApiSampleResource<GeoCodeResponse>> {
        val responseData = MutableLiveData<ApiSampleResource<GeoCodeResponse>>()
        if (networkHelper.isNetworkConnected()) {
           // val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader2.geoCodeApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<GeoCodeResponse>(data)
                                responseData.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseData.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseData.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            responseData.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            responseData.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseData.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseData.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseData.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseData
    }

}


