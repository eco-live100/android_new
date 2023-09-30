package com.app.ecolive.pharmacy_module.PharmacyViewModel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import com.app.ecolive.pharmacy_module.model.CreatHospitalModel
import com.app.ecolive.pharmacy_module.model.CreateHealthModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import okhttp3.MultipartBody

class PharmacyViewModel(activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)



    private lateinit var createHealthModel: LiveData<ApiSampleResource<CreateHealthModel>>
    private lateinit var getCommonMedication: LiveData<ApiSampleResource<CommonMedicationModel>>
    private lateinit var registerHospital: LiveData<ApiSampleResource<CreatHospitalModel>>


    fun creatHealthProfile(json: MultipartBody): LiveData<ApiSampleResource<CreateHealthModel>> {
        createHealthModel = webServiceRepository.createHealthProfileApi(json)
        return createHealthModel
    }
    fun getCommonMedicationApi(): LiveData<ApiSampleResource<CommonMedicationModel>> {
        getCommonMedication = webServiceRepository.getCommonMedicationApi()
        return getCommonMedication
    }

    fun getCommonMedicationData( ): LiveData<ApiSampleResource<CommonMedicationModel>> {
        return getCommonMedication
    }

    fun creatHospital(json: MultipartBody): LiveData<ApiSampleResource<CreatHospitalModel>> {
        registerHospital = webServiceRepository.creatHospitalApi(json)
        return registerHospital
    }
}