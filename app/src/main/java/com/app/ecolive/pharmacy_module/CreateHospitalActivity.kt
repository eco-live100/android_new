package com.app.ecolive.pharmacy_module

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCreateHospitalBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
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
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date


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
    var latitude = "0"
    var longitude = "0"
    var address = ""
    var selectedProfession = ""
    private val myCalendar: Calendar = Calendar.getInstance()
    private var selectedTime = 1
    private var primaryVisitingHour = ""
    private var secondaryVisitingHour = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        Utils.changeStatusColor(this, R.color.darkblue)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_hospital)
        binding.toolbar.toolbarTitle.text = "Create Profile"
        binding.toolbar.ivBack.setOnClickListener { finish() }
        //  startActivity(Intent(this,HospitalProfile::class.java))


        selectedProfession = ""
        binding.professionRG.setOnCheckedChangeListener { group, _ ->

            when (group.checkedRadioButtonId) {
                R.id.doctorRB -> {
                    selectedProfession = getString(R.string.doctor)
                }

                R.id.nurseLpnRB -> {
                    selectedProfession = getString(R.string.nurseLpn)
                }

                R.id.nurseNpRB -> {
                    selectedProfession = getString(R.string.nurseNp)
                }

                R.id.pharmacistRB -> {
                    selectedProfession = getString(R.string.pharmacist)
                }

                R.id.dentistRB -> {
                    selectedProfession = getString(R.string.dentist)
                }
            }
        }
        val myTimeListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                if (view.isShown) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    myCalendar.set(Calendar.MINUTE, minute)
                }
                when (selectedTime) {
                    1 -> {
                        binding.fromVisitingTimeTv.text =
                            DateFormat.format("hh:mm aaa", myCalendar.time).toString()
                    }

                    2 -> {
                        binding.toVisitingTimeTv.text =
                            DateFormat.format("hh:mm aaa", myCalendar.time).toString()
                    }

                    3 -> {
                        binding.fromAnotherVisitingTimeTv.text =
                            DateFormat.format("hh:mm aaa", myCalendar.time).toString()
                    }

                    else -> {
                        binding.toAnotherVisitingTimeTv.text =
                            DateFormat.format("hh:mm aaa", myCalendar.time).toString()
                    }
                }
            }
        binding.fromVisitingTimeIcon.setOnClickListener {
            selectedTime = 1
            val timePickerDialog = TimePickerDialog(
                this,
                R.style.my_dialog_theme,
                myTimeListener,
                myCalendar[Calendar.HOUR],
                myCalendar[Calendar.MINUTE],
                false
            )

            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        binding.toVisitingTimeIcon.setOnClickListener {
            selectedTime = 2
            val timePickerDialog = TimePickerDialog(
                this,
                R.style.my_dialog_theme,
                myTimeListener,
                myCalendar[Calendar.HOUR],
                myCalendar[Calendar.MINUTE],
                false
            )

            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        binding.fromAnotherVisitingTimeIcon.setOnClickListener {
            selectedTime = 3
            val timePickerDialog = TimePickerDialog(
                this,
                R.style.my_dialog_theme,
                myTimeListener,
                myCalendar[Calendar.HOUR],
                myCalendar[Calendar.MINUTE],
                false
            )

            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        binding.toAnotherVisitingTimeIcon.setOnClickListener {
            selectedTime = 4
            val timePickerDialog = TimePickerDialog(
                this,
                R.style.my_dialog_theme,
                myTimeListener,
                myCalendar[Calendar.HOUR],
                myCalendar[Calendar.MINUTE],
                false
            )

            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        binding.createBtn.setOnClickListener {
            primaryVisitingHour = timeDifference(
                date1 = binding.fromVisitingTimeTv.text.toString(),
                date2 = binding.toVisitingTimeTv.text.toString()
            )
            secondaryVisitingHour = timeDifference(
                date1 = binding.fromAnotherVisitingTimeTv.text.toString(),
                date2 = binding.toAnotherVisitingTimeTv.text.toString()
            )
            if (binding.hospitalName.text.toString() == "") {
                Toast.makeText(this, "Enter hospital name", Toast.LENGTH_SHORT).show()

            } else if (binding.mobileNumber.text.toString() == "") {
                Toast.makeText(this, "Enter mobileNumber", Toast.LENGTH_SHORT).show()

            } else if (binding.services.text.toString() == "") {
                Toast.makeText(this, "Enter services", Toast.LENGTH_SHORT).show()

            } else if (selectedProfession.isEmpty()) {
                Toast.makeText(this, "Please select Profession", Toast.LENGTH_SHORT).show()
            }else if (primaryVisitingHour.isEmpty()) {
                Toast.makeText(this, "Please select primary hours", Toast.LENGTH_SHORT).show()
            }else if (secondaryVisitingHour.isEmpty()) {
                Toast.makeText(this, "Please select secondary hours", Toast.LENGTH_SHORT).show()
            } else if (binding.consultFee.text.toString() == "") {
                Toast.makeText(this, "Enter consultFee", Toast.LENGTH_SHORT).show()

            } else if (binding.hospitalLocation.text.toString() == "") {
                Toast.makeText(this, "Enter hospitalLocation", Toast.LENGTH_SHORT).show()

            } else {
                createHospital()
            }

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
            option = 3
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

    private fun timeDifference(date1: String, date2: String): String {
        val format = SimpleDateFormat("hh:mm aaa")
        val date1Cov: Date = format.parse(date1)
        val date2Cov: Date = format.parse(date2)
        val diff = ""
        try {
            val millis: Long = date1Cov.time - date2Cov.time
            val hours: Int = (millis / (1000 * 60 * 60)).toInt()
            val mins = (millis / (1000 * 60) % 60).toInt()
            val diff = "$hours:$mins"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return diff
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
                if (option == 1 || option == 2) {
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
                        } else if (option == 2) {
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
                } else {
                    val place = Autocomplete.getPlaceFromIntent(it.data!!)
                    binding.hospitalLocation.text = place.address
                    latitude = place.latLng.latitude.toString()
                    longitude = place.latLng.longitude.toString()
                    address = (place.address)
                }


            }
        }


    private fun createHospital() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        var pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("fullName", binding.hospitalName.text.toString())
        builder.addFormDataPart("mobileNumber", binding.hospitalName.text.toString())
        builder.addFormDataPart("services", binding.services.text.toString())
        builder.addFormDataPart("idNumber", binding.idNumberTv.text.toString())
        builder.addFormDataPart("professionType", selectedProfession)
        builder.addFormDataPart("consultFees", binding.consultFee.text.toString())
        builder.addFormDataPart("latitude", latitude)
        builder.addFormDataPart("longitude", longitude)
        builder.addFormDataPart("address", address)
        builder.addFormDataPart("primaryVisitingHour", primaryVisitingHour)
        builder.addFormDataPart("secondaryVisitingHour", secondaryVisitingHour)
        builder.addFormDataPart("allowPublicToViewEmployees", "${binding.saveAndRepeat.isChecked}")
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
                        startActivity(Intent(this,HospitalProfile::class.java))
                        finish()
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
        val reqFile = filePath.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        multipartBody.add(MultipartBody.Part.createFormData("picture", filePath.name, reqFile))

    }

    private fun setBodyLogo() {
        val filePath = File(logoImage)
        val reqFile = filePath.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        multipartBody.add(MultipartBody.Part.createFormData("picture", filePath.name, reqFile))

    }


}