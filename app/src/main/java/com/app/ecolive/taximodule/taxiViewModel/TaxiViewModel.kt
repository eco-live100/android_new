package com.app.ecolive.taximodule.taxiViewModel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.taximodule.model.CommonModel
import com.app.ecolive.taximodule.model.ConfirmTaxiModel
import com.app.ecolive.taximodule.model.ScheduleRideModel
import com.app.ecolive.taximodule.model.TaxiBookingRequestList
import com.app.ecolive.taximodule.model.VehicleModel
import org.json.JSONObject

class TaxiViewModel(activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)

    private lateinit var getVehicalData: LiveData<ApiSampleResource<VehicleModel>>
    private lateinit var confirmViewModel: LiveData<ApiSampleResource<ConfirmTaxiModel>>
    private lateinit var scheduleRideViewModel: LiveData<ApiSampleResource<ScheduleRideModel>>
    private lateinit var getTaxiBookingRequestListViewModel: LiveData<ApiSampleResource<TaxiBookingRequestList>>
    private lateinit var acceptBookingRequestData: LiveData<ApiSampleResource<CommonModel>>
    private lateinit var declineBookingRequestData: LiveData<ApiSampleResource<CommonModel>>
    private lateinit var completeBookingRequestData: LiveData<ApiSampleResource<CommonModel>>
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

    fun acceptBookingRequestRideApi(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        acceptBookingRequestData = webServiceRepository.acceptBookingRequestRideApi(json)
        return acceptBookingRequestData
    }

    fun declineBookingRequestRideApi(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        declineBookingRequestData = webServiceRepository.declineBookingRequestRideApi(json)
        return declineBookingRequestData
    }

    fun completeBookingRequestRideApi(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        completeBookingRequestData = webServiceRepository.completeBookingRequestRideApi(json)
        return completeBookingRequestData
    }
}