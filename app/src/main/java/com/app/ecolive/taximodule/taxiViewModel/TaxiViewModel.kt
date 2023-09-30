package com.app.ecolive.taximodule.taxiViewModel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.pharmacy_module.model.CreateHealthModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.taximodule.model.ConfirmTaxiModel
import com.app.ecolive.taximodule.model.ScheduleRideModel
import com.app.ecolive.taximodule.model.TaxiBookingRequestList
import com.app.ecolive.taximodule.model.VehicleModel
import okhttp3.MultipartBody
import org.json.JSONObject

class TaxiViewModel(activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)

    private lateinit var getVehicalData: LiveData<ApiSampleResource<VehicleModel>>
    private lateinit var confirmViewModel: LiveData<ApiSampleResource<ConfirmTaxiModel>>
    private lateinit var scheduleRideViewModel: LiveData<ApiSampleResource<ScheduleRideModel>>
    private lateinit var getTaxiBookingRequestListViewModel: LiveData<ApiSampleResource<TaxiBookingRequestList>>

    fun getVehicalApi(json: JSONObject): LiveData<ApiSampleResource<VehicleModel>> {
        getVehicalData = webServiceRepository.getVehicalApi(json)
        return getVehicalData
    }

    fun confirmTaxi(json: JSONObject): LiveData<ApiSampleResource<ConfirmTaxiModel>> {
        confirmViewModel = webServiceRepository.confirmTaxiApi(json)
        return confirmViewModel
    }

    fun scheduleRideTaxi(json: JSONObject): LiveData<ApiSampleResource<ScheduleRideModel>> {
        scheduleRideViewModel = webServiceRepository.scheduleRideApi(json)
        return scheduleRideViewModel
    }

    fun getTaxiBookingRequestList(json: JSONObject): LiveData<ApiSampleResource<TaxiBookingRequestList>> {
        getTaxiBookingRequestListViewModel = webServiceRepository.getTaxiBookingRequestListApi(json)
        return getTaxiBookingRequestListViewModel
    }
}