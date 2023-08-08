package com.app.ecolive.taximodule.taxiViewModel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.pharmacy_module.model.CreateHealthModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.taximodule.model.VehicleModel
import okhttp3.MultipartBody
import org.json.JSONObject

class TaxiViewModel(activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)

    private lateinit var getVehicalData: LiveData<ApiSampleResource<VehicleModel>>

    fun getVehicalApi(json: JSONObject): LiveData<ApiSampleResource<VehicleModel>> {
        getVehicalData = webServiceRepository.getVehicalApi(json)
        return getVehicalData
    }
}