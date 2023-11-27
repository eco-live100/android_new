package com.app.ecolive.taximodule.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecolive.service.ResponseStatusCallbacks

class MainActivityViewModel : ViewModel() {


    private val _getLocationInformation = MutableLiveData<ResponseStatusCallbacks<LocationModelA>>()
    val getLocationInformation: LiveData<ResponseStatusCallbacks<LocationModelA>>
        get() = _getLocationInformation

    fun getLocationInfo(context: Context, latitude: String, longitude: String) {
        GeoCoderUtil.execute(context, latitude, longitude, object :
            LoadDataCallback<LocationModelA> {
            override fun onDataLoaded(response: LocationModelA) {
                _getLocationInformation.value = ResponseStatusCallbacks.success(response)
            }
            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                _getLocationInformation.value =
                    ResponseStatusCallbacks.error(data = null, message = "Something went wrong!")
            }
        })
    }
}