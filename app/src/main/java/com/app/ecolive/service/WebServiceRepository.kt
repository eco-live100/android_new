package com.app.ecolive.service

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ecolive.R
import com.app.ecolive.login_module.LoginModel
import com.app.ecolive.login_module.model.BaseModel
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import com.app.ecolive.pharmacy_module.model.CreatHospitalModel
import com.app.ecolive.pharmacy_module.model.CreateHealthModel
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.shop_owner.model.*
import com.app.ecolive.taximodule.model.VehicleModel
import com.app.ecolive.user_module.model.AddressModel
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.AppConstant.INTERNAL_ERROR
import com.app.ecolive.utils.AppConstant.NO_INTERNET
import com.app.ecolive.utils.AppConstant.PARSING_ERROR
import com.google.gson.Gson
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
    private var networkHelper: NetworkHelper = NetworkHelper(application)
    var application = application

    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun userSignUp(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userSignupAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 ,201-> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
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
                        205,400,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(),vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun sendOtpMobile(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.sendMobileOtpAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 ,201-> {
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
                        205,400,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun verifyOtp(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.verifyMobileOtpAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 ,201-> {
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
                        205,400,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun userLogin(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val loginResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userLoginAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
                                loginResponseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                loginResponseModel.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            loginResponseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                       201,205,400,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getString("message")
                           loginResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                        }
                        500->{
                            loginResponseModel.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        loginResponseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        loginResponseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else loginResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return loginResponseModel
    }

    fun resetPassword(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.resetPassword(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 ,201-> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
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
                        205,400,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(),vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }


    fun userSocialLogin(map: JSONObject): LiveData<ApiSampleResource<LoginModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<LoginModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.userSocialLoginAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<LoginModel>(data)
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
                        201,205,400,404,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun vehicleCategoriesList(): LiveData<ApiSampleResource<VehicalCatgryListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<VehicalCatgryListModel>>()
        if (networkHelper.isNetworkConnected()) {
          //  val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.vehiclecategorieslistAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<VehicalCatgryListModel>(data)
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
                        201,205,400,404,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun shopCategoriesList(): LiveData<ApiSampleResource<ShopCategryListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ShopCategryListModel>>()
        if (networkHelper.isNetworkConnected()) {
            //  val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopcategorieslistAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ShopCategryListModel>(data)
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
                        201,205,400,404,401,408,409-> {
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
                        else->{
                        venueListResponseModel.postValue( ApiSampleResource.error(
                            response.code(),
                            application.resources.getString(R.string.technical_issue),
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun updateVehicleProfile(requestBody: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.updateVehicleProfileAPI(requestBody)
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
                        201 -> {
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
                        205,400,401,408,409-> {
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
    }

    fun updateShopSignup(requestBody: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopUploadAPI(requestBody)
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
                        201 -> {
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
                        205,400,401,408,409-> {
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
    }

    fun shopSignup(map: JSONObject): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
              val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopSignUpAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201 -> {
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
                        200,205,400,404,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }


    fun introPage(): LiveData<ApiSampleResource<IntroModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<IntroModel>>()
        if (networkHelper.isNetworkConnected()) {
        //    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.introAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<IntroModel>(data)
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
                        201,205,400,404,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }
    fun shopList(): LiveData<ApiSampleResource<ShopListModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ShopListModel>>()
        if (networkHelper.isNetworkConnected()) {
            //    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.shopListAPI()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ShopListModel>(data)
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
                        201,205,400,404,401,408,409-> {
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }


    fun attributeList(json: JSONObject): LiveData<ApiSampleResource<AttributeModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<AttributeModel>>()
        if (networkHelper.isNetworkConnected()) {
              val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.attributeListAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AttributeModel>(data)
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
                        201,205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                          //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                           // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }


    fun addProduct(body: MultipartBody): LiveData<ApiSampleResource<BaseModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<BaseModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addProductListAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201 -> {
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
                         205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }


    fun vendorShopProductList(json: JSONObject): LiveData<ApiSampleResource<ProductModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.vendorShopProductListAPI(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductModel>(data)
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
                        201,205,400,404,401,408,409-> {
                            try {
                                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                                var vv=jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            }catch (e:Exception){}

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
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
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addAddres(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddressModel>(data)
                                responseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        201,205,400,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getString("message")
                            responseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                        }
                        500->{
                            responseModel.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseModel
    }

    fun getAddress(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<AddressModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getAddres()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddressModel>(data)
                                responseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        201,205,400,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getString("message")
                            responseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                        }
                        500->{
                            responseModel.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseModel
    }

    fun deleteAddress(map: JSONObject): LiveData<ApiSampleResource<AddressModel>> {
        val responseModel = MutableLiveData<ApiSampleResource<AddressModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), map.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.deleteAddress(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<AddressModel>(data)
                                responseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                responseModel.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            responseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        201,205,400,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getString("message")
                            responseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                        }
                        500->{
                            responseModel.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        responseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        responseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else responseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return responseModel
    }

    fun addToCart(json: JSONObject): LiveData<ApiSampleResource<ProductModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<ProductModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.addToCart(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<ProductModel>(data)
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
                        201,205,400,404,401,408,409-> {
                            try {
                                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                                var vv=jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            }catch (e:Exception){}

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun getCart(json: JSONObject): LiveData<ApiSampleResource<GetCartModel>> {
        val  ResponseModel = MutableLiveData<ApiSampleResource<GetCartModel>>()
        if (networkHelper.isNetworkConnected()) {
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getCart()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<GetCartModel>(data)
                                ResponseModel.postValue(ApiSampleResource.success(response.code(),response.message(),dataResponse))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                ResponseModel.postValue(ApiSampleResource.error(
                                    PARSING_ERROR,
                                    application.resources.getString(R.string.Parsing_Problem),
                                    null))
                            }
                        }
                        204->{
                            ResponseModel.postValue(ApiSampleResource.error(response.code(), application.resources.getString(R.string.No_data_found), null))
                        }
                        201,205,400,404,401,408,409-> {
                            try {
                                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                                var vv=jsonObj.getString("message")
                                //  var vv=jsonObj.getJSONObject("message").getString("msg")
                                ResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            }catch (e:Exception){}

                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
                        }
                        500->{
                            ResponseModel.postValue( ApiSampleResource.error(
                                response.code(),
                                application.resources.getString(R.string.Internal_server_error),
                                null))

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        ResponseModel.postValue(ApiSampleResource.error(INTERNAL_ERROR, application.resources.getString(R.string.Network_Failure), null))
                    } else {
                        ResponseModel.postValue(ApiSampleResource.error(PARSING_ERROR, application.resources.getString(R.string.Something_went_wrong), null))
                    }
                }

            })
        } else ResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return ResponseModel
    }


    ///pharmacy
    fun createHealthProfileApi(body: MultipartBody): LiveData<ApiSampleResource<CreateHealthModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CreateHealthModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.createHealthProfileApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CreateHealthModel>(data)
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
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }


    fun getCommonMedicationApi(): LiveData<ApiSampleResource<CommonMedicationModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CommonMedicationModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getCommonMedicationApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CommonMedicationModel>(data)
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
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }

    fun creatHospitalApi(body: MultipartBody): LiveData<ApiSampleResource<CreatHospitalModel>> {
        val venueListResponseModel = MutableLiveData<ApiSampleResource<CreatHospitalModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.creatHospitalApi(body)
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<CreatHospitalModel>(data)
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
                        205,400,404,401,408,409-> {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            var vv=jsonObj.getJSONObject("message")
                            //  var vv=jsonObj.getJSONObject("message").getString("msg")
                            venueListResponseModel.postValue(ApiSampleResource.error(response.code(), jsonObj.getString("message"), null))
                            // venueListResponseModel.postValue(ApiSampleResource.error(response.code(), vv, null))
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
        } else venueListResponseModel.postValue(ApiSampleResource.error(
            AppConstant.NO_INTERNET, application.resources.getString(R.string.No_Internet), null))
        return venueListResponseModel
    }
    ///Taxi

    fun getVehicalApi(body: JSONObject): LiveData<ApiSampleResource<VehicleModel>> {
        val responseData = MutableLiveData<ApiSampleResource<VehicleModel>>()
        if (networkHelper.isNetworkConnected()) {
//            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val responseBody: Call<ResponseBody> = apiInterfaceHeader.getVehicalApi()
            responseBody.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    when (response.code()) {
                        201,200 -> {
                            val data = response.body()?.string()!!
                            try {
                                val dataResponse = fromJson<VehicleModel>(data)
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


