package com.app.ecolive.pharmacy_module.PharmacyViewModel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import com.app.ecolive.pharmacy_module.model.CreatHospitalModel
import com.app.ecolive.pharmacy_module.model.CreateHealthModel
import com.app.ecolive.pharmacy_module.model.DoctorListModel
import com.app.ecolive.pharmacy_module.model.RegisterDoctorModel
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import okhttp3.MultipartBody
import org.json.JSONObject

class PharmacyViewModel(activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)



    private lateinit var createHealthModel: LiveData<ApiSampleResource<CreateHealthModel>>
    private lateinit var getCommonMedication: LiveData<ApiSampleResource<CommonMedicationModel>>
    private lateinit var registerHospitalEmployee: LiveData<ApiSampleResource<RegisterDoctorModel>>
    private lateinit var getProfileViewModel: LiveData<ApiSampleResource<CreatHospitalModel>>
    private lateinit var getDoctorListModel: LiveData<ApiSampleResource<DoctorListModel>>
    private lateinit var searchMedicationListModel: LiveData<ApiSampleResource<CommonMedicationModel>>


    fun creatHealthProfile(json: MultipartBody): LiveData<ApiSampleResource<CreateHealthModel>> {
        createHealthModel = webServiceRepository.createHealthProfileApi(json)
        return createHealthModel
    }
    fun getCommonMedicationApi(): LiveData<ApiSampleResource<CommonMedicationModel>> {
        getCommonMedication = webServiceRepository.getCommonMedicationApi()
        return getCommonMedication
    }
    fun searchMedicineApi(): LiveData<ApiSampleResource<CommonMedicationModel>> {
        searchMedicationListModel = webServiceRepository.searchMedicineApi()
        return searchMedicationListModel
    }

   fun registerHospitalEmployeeApi(json: MultipartBody): LiveData<ApiSampleResource<RegisterDoctorModel>> {
        registerHospitalEmployee = webServiceRepository.registerHospitalEmployeeApi(json)
        return registerHospitalEmployee
    }

    fun getProfile(userId: String,professionType:String): LiveData<ApiSampleResource<CreatHospitalModel>> {
        getProfileViewModel = webServiceRepository.getProfileApi(userId = userId, professionType = professionType)
        return getProfileViewModel
    }
    fun getDoctorListApi(json: JSONObject): LiveData<ApiSampleResource<DoctorListModel>> {
        getDoctorListModel = webServiceRepository.getDoctorListApi(json)
        return getDoctorListModel
    }
}