package com.app.ecolive.pharmacy_module

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCreateHospitalBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.VehicalListActivity
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.nightout.ui.fragment.SelectSourceBottomSheetFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CreateHospitalActivity : AppCompatActivity(), OnSelectOptionListener {
    lateinit var binding: ActivityCreateHospitalBinding
    private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
    var bgimg = 1
    var logo = 2
    var option = 1
    var backgroundImg = ""
    var logoImage = ""
    var multipartBody: ArrayList<MultipartBody.Part?> = ArrayList()
    private val progressDialog = CustomProgressDialog()
    var latitude ="0"
    var longitude ="0"
    var address =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_hospital)
        binding.toolbar.toolbarTitle.text ="Create Hospital"
        binding.toolbar.ivBack.setOnClickListener { finish() }
      //  startActivity(Intent(this,HospitalProfile::class.java))

        binding.createBtn.setOnClickListener {
            startActivity(Intent(this,HospitalProfile::class.java))
         /*   if (binding.hospitalName.text.toString() == ""){
                Toast.makeText(this, "Enter hospital name", Toast.LENGTH_SHORT).show()

            }else if (binding.mobileNumber.text.toString() == ""){
                Toast.makeText(this, "Enter mobileNumber", Toast.LENGTH_SHORT).show()

            }else if (binding.services.text.toString() == ""){
                Toast.makeText(this, "Enter services", Toast.LENGTH_SHORT).show()

            }else if (binding.consultfee.text.toString() == ""){
                Toast.makeText(this, "Enter consultfee", Toast.LENGTH_SHORT).show()

            }else if (binding.hospitalLocation.text.toString() == ""){
                Toast.makeText(this, "Enter hospitalLocation", Toast.LENGTH_SHORT).show()

            }else{
               CreatHospital()
            }*/

        }
        binding.backgroundImg.setOnClickListener {
            onSelectImage()
            option = bgimg
        }
        binding.logoImage.setOnClickListener {
            onSelectImage()
            option = logo
        }

        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        binding.hospitalLocation.setOnClickListener(View.OnClickListener {
            option =3
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

             receiveData.launch(intent)
        })
    }

    private fun onSelectImage() {
        if (!Utils.checkingPermissionIsEnabledOrNot(this)) {
            Utils.requestMultiplePermission(this, VehicleInfoActivity.requestPermissionCode)
        } else {
            selectSourceBottomSheetFragment = SelectSourceBottomSheetFragment(this, "")
            selectSourceBottomSheetFragment.show(
                this.supportFragmentManager,
                "selectSourceBottomSheetFragment"
            )
        }
    }

    override fun onOptionSelect(option: String) {
        if (option == AppConstant.CAMERA_KEY) {
            selectSourceBottomSheetFragment.dismiss()
            //  ImagePicker.onCaptureImage(this)
            val intent = Lassi(this)
                .with(LassiOption.CAMERA)
                .setMaxCount(1)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE)
                .setCompressionRation(10)
                .build()

            receiveData.launch(intent)

        } else if (option == AppConstant.GALLERY_KEY) {
            selectSourceBottomSheetFragment.dismiss()
            val intent = Lassi(this)
                .with(LassiOption.GALLERY)
                .setMaxCount(1)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE)
                .setCompressionRation(10)
                .build()

            receiveData.launch(intent)
        }
    }

    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (option == 1||option==2){
                    val selectedMedia =
                        it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    if (!selectedMedia.isNullOrEmpty()) {
                        //storePath
                        if (option == 1) {
                            backgroundImg = selectedMedia[0].path!!
                            val bitmap: Bitmap?
                            bitmap = BitmapFactory.decodeFile(selectedMedia[0].path)
                            //imageUrl = Uri.parse(selectedMedia[0].path)

                            try {
                                binding.backgroundImg.setImageBitmap(null)
                                binding.backgroundImg.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                Log.d("crashImage", "onActivityResult: " + e)
                            }
                        } else if(option==2){
                            logoImage = selectedMedia[0].path!!
                            val bitmap: Bitmap?
                            bitmap = BitmapFactory.decodeFile(selectedMedia[0].path)
                            //imageUrl = Uri.parse(selectedMedia[0].path)

                            try {
                                binding.logoImage.setImageBitmap(null)
                                binding.logoImage.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                Log.d("crashImage", "onActivityResult: " + e)
                            }
                        }



                        //  setBody(bitmap!!, "vehicleDocument")

                    }
                }else{
                    val place = Autocomplete.getPlaceFromIntent(it.data!!)
                    binding.hospitalLocation.text = place.address
                    latitude =place.latLng.latitude.toString()
                    longitude =place.latLng.longitude.toString()
                    address =(place.address)
                }


            }
        }


    private fun CreatHospital() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        var pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("fullName", binding.hospitalName.text.toString())
        builder.addFormDataPart("latitude", latitude)
        builder.addFormDataPart("longitude", longitude)
        builder.addFormDataPart("address", address)
        builder.addFormDataPart("cosultationFees", binding.consultfee.text.toString())
        builder.addFormDataPart("services", binding.services.text.toString())
        builder.addFormDataPart("allowPublicToViewEmployees", "false")
      //  builder.addFormDataPart("medications", arrayListOf<String>("test","test").toString())

        if (multipartBody != null) {
            setBodyBgImg()
            setBodyLogo()
            builder.addPart(multipartBody[0]!!.body)
            builder.addPart(multipartBody[1]!!.body)
        }

        pharmacyViewModel.creatHospital(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {

                    }
                }
                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun setBodyBgImg() {
        val filePath = File(backgroundImg)
        val reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), filePath)
        multipartBody.add(MultipartBody.Part.createFormData("picture", filePath.name, reqFile))

    }
    private fun setBodyLogo() {
        val filePath = File(logoImage)
        val reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), filePath)
        multipartBody.add(MultipartBody.Part.createFormData("picture", filePath.name, reqFile))

    }


}