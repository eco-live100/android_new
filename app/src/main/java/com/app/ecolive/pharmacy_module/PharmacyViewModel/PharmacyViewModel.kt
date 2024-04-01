package com.app.ecolive.pharmacy_module.PharmacyViewModel

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import com.app.ecolive.pharmacy_module.model.AddMedicineModel
import com.app.ecolive.pharmacy_module.model.AllOrderModel
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import com.app.ecolive.pharmacy_module.model.CreateHealthProfileModel
import com.app.ecolive.pharmacy_module.model.DoctorListModel
import com.app.ecolive.pharmacy_module.model.DoctorProfileModel
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
import com.app.ecolive.service.ApiSampleResource
import com.app.ecolive.service.WebServiceRepository
import com.app.ecolive.taximodule.model.CommonModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class PharmacyViewModel(activity: Activity) : BaseObservable() {
    private var webServiceRepository = WebServiceRepository(activity)


    private lateinit var createHealthModel: LiveData<ApiSampleResource<CreateHealthProfileModel>>
    private lateinit var getHealthModel: LiveData<ApiSampleResource<HealthProfileModel>>
    private lateinit var getCommonMedication: LiveData<ApiSampleResource<CommonMedicationModel>>
    private lateinit var placeOrderModel: LiveData<ApiSampleResource<CommonModel>>
    private lateinit var registerHospitalEmployee: LiveData<ApiSampleResource<DoctorProfileModel>>
    private lateinit var getDoctorProfileViewModel: LiveData<ApiSampleResource<DoctorProfileModel>>
    private lateinit var getDoctorListModel: LiveData<ApiSampleResource<DoctorListModel>>
    private lateinit var getPharmacyListModel: LiveData<ApiSampleResource<PharmacyListModel>>
    private lateinit var searchMedicationListModel: LiveData<ApiSampleResource<SearchMedicineList>>
    private lateinit var requestPrescriptionModel: LiveData<ApiSampleResource<RequestPrescriptionModel>>
    private lateinit var userPrescriptionListModel: LiveData<ApiSampleResource<UserPrescriptionModel>>
    private lateinit var prescriptionDetailData: LiveData<ApiSampleResource<PrescriptionRequestData>>
    private lateinit var cancelPrescriptionModel: LiveData<ApiSampleResource<CommonModel>>
    private lateinit var startPrescriptionModel: LiveData<ApiSampleResource<CommonModel>>
    private lateinit var createAndUpdatePharmacyProfileViewModel: LiveData<ApiSampleResource<PharmacyProfileModel>>
    private lateinit var pharmacyProfileViewModel: LiveData<ApiSampleResource<PharmacyProfileModel>>
    private lateinit var addMedicineViewModel: LiveData<ApiSampleResource<AddMedicineModel>>
    private lateinit var medicineListViewModel: LiveData<ApiSampleResource<MedicineListModel>>
    private lateinit var pharmacyStatusViewModel: LiveData<ApiSampleResource<PharmacyProfileModel>>
    private lateinit var doctorPrescriptionListModel: LiveData<ApiSampleResource<PrescriptionListModel>>

    private lateinit var getAllOrderListModel: LiveData<ApiSampleResource<AllOrderModel>>


    fun createHealthProfile(json: MultipartBody): LiveData<ApiSampleResource<CreateHealthProfileModel>> {
        createHealthModel = webServiceRepository.createHealthProfileApi(json)
        return createHealthModel
    }

    fun getCommonMedicationApi(): LiveData<ApiSampleResource<CommonMedicationModel>> {
        getCommonMedication = webServiceRepository.getCommonMedicationApi()
        return getCommonMedication
    }

    fun searchMedicineApi(search: String): LiveData<ApiSampleResource<SearchMedicineList>> {
        searchMedicationListModel = webServiceRepository.searchMedicineApi(search = search)
        return searchMedicationListModel
    }

    fun registerHospitalEmployeeApi(json: MultipartBody): LiveData<ApiSampleResource<DoctorProfileModel>> {
        registerHospitalEmployee = webServiceRepository.registerHospitalEmployeeApi(json)
        return registerHospitalEmployee
    }

    fun placeOrderApi(json: MultipartBody): LiveData<ApiSampleResource<CommonModel>> {
        placeOrderModel = webServiceRepository.placeOrderApi(json)
        return placeOrderModel
    }
    fun acceptOrder(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        placeOrderModel = webServiceRepository.acceptOrder(json)
        return placeOrderModel
    }
    fun updateMedicalOrderByPharmacy(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        placeOrderModel = webServiceRepository.updateMedicalOrderByPharmacy(json)
        return placeOrderModel
    }

    /*   fun requestPrescriptionApi(json: MultipartBody): LiveData<ApiSampleResource<RequestPrescriptionModel>> {
           requestPrescriptionModel = webServiceRepository.requestPrescriptionApi(json)
           return requestPrescriptionModel
       }*/
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
        medication: ArrayList<SearchMedicineListData>,
        attachment: MultipartBody.Part,
        picture: MultipartBody.Part,
    ): LiveData<ApiSampleResource<RequestPrescriptionModel>> {
        requestPrescriptionModel = webServiceRepository.requestPrescriptionApi(
            doctorId = doctorId,
            symptomDescription = symptomDescription,
            symptomDuration = symptomDuration,
            alreadyMedication = alreadyMedication,
            recentMedicalHistory = recentMedicalHistory,
            allergies = allergies,
            sendPrescriptionToPharmacy = sendPrescriptionToPharmacy,
            habits = habits,
            otherRelaventINfotmation = otherRelaventINfotmation,
            medication = medication,
            attachment = attachment,
            picture = picture
        )
        return requestPrescriptionModel
    }

    fun getProfile(): LiveData<ApiSampleResource<DoctorProfileModel>> {
        getDoctorProfileViewModel =
            webServiceRepository.getDoctorProfileApi(/*userId = userId, professionType = professionType*/)
        return getDoctorProfileViewModel
    }

    fun getHealthProfile(): LiveData<ApiSampleResource<HealthProfileModel>> {
        getHealthModel = webServiceRepository.getHealthProfile()
        return getHealthModel
    }

    fun getDoctorListApi(): LiveData<ApiSampleResource<DoctorListModel>> {
        getDoctorListModel = webServiceRepository.getDoctorListApi()
        return getDoctorListModel
    }

    fun getPharmacyListApi(
        lat: Double,
        long: Double,
        distance: Int,
        //keyword: String,
        page: Int,
        limit: Int,
    ): LiveData<ApiSampleResource<PharmacyListModel>> {
        getPharmacyListModel = webServiceRepository.getPharmacyListApi(lat = lat, long = long, distance = distance, page = page, limit = limit)
        return getPharmacyListModel
    }
    fun getAllReadyOrdersForDriver(
        lat: Double,
        long: Double,
        distance: Int,
        //keyword: String,
        page: Int,
        limit: Int,
    ): LiveData<ApiSampleResource<PharmacyListModel>> {
        getPharmacyListModel = webServiceRepository.getAllReadyOrdersForDriver(lat = lat, long = long, distance = distance, page = page, limit = limit)
        return getPharmacyListModel
    }
    fun getAllOrderApi(): LiveData<ApiSampleResource<AllOrderModel>> {
        getAllOrderListModel = webServiceRepository.getAllOrderApi()
        return getAllOrderListModel
    }

    fun userPrescriptionListApi(): LiveData<ApiSampleResource<UserPrescriptionModel>> {
        userPrescriptionListModel = webServiceRepository.userPrescriptionListApi()
        return userPrescriptionListModel
    }

    fun prescriptionDetailApi(json: JSONObject): LiveData<ApiSampleResource<PrescriptionRequestData>> {
        prescriptionDetailData = webServiceRepository.prescriptionDetailApi(json)
        return prescriptionDetailData
    }

    fun startPrescriptionApi(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        startPrescriptionModel = webServiceRepository.startPrescriptionApi(json)
        return startPrescriptionModel
    }

    fun cancelPrescriptionApi(json: JSONObject): LiveData<ApiSampleResource<CommonModel>> {
        cancelPrescriptionModel = webServiceRepository.cancelPrescriptionApi(json)
        return cancelPrescriptionModel
    }

    fun createAndUpdatePharmacyApi(json: MultipartBody): LiveData<ApiSampleResource<PharmacyProfileModel>> {
        createAndUpdatePharmacyProfileViewModel =
            webServiceRepository.createAndUpdatePharmacyApi(json)
        return createAndUpdatePharmacyProfileViewModel
    }

    fun getPharmacyProfile(): LiveData<ApiSampleResource<PharmacyProfileModel>> {
        pharmacyProfileViewModel = webServiceRepository.getPharmacyProfile()
        return pharmacyProfileViewModel
    }

    fun addMedicineApi(json: MultipartBody): LiveData<ApiSampleResource<AddMedicineModel>> {
        addMedicineViewModel = webServiceRepository.addMedicineApi(json)
        return addMedicineViewModel
    }

    fun getMedicineListApi(userId: String): LiveData<ApiSampleResource<MedicineListModel>> {
        medicineListViewModel = webServiceRepository.getMedicineListApi(userId = userId)
        return medicineListViewModel
    }

    fun updatePharmacyStatusApi(pharmacyId: String): LiveData<ApiSampleResource<PharmacyProfileModel>> {
        pharmacyStatusViewModel = webServiceRepository.updatePharmacyStatus(pharmacyId = pharmacyId)
        return pharmacyStatusViewModel
    }

    fun getPrescriptionRequestForDoctorApi(json: JSONObject): LiveData<ApiSampleResource<PrescriptionListModel>> {
        doctorPrescriptionListModel = webServiceRepository.getPrescriptionRequestForDoctorApi(json)
        return doctorPrescriptionListModel
    }

}