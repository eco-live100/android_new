package com.app.ecolive.pharmacy_module

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCreateHospitalBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//@AndroidEntryPoint
class CreatePharmacyProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateHospitalBinding
    private var bgimg = 1
    private var logo = 2
    var option = 1
    private var backgroundImg: Uri? = null
    private var logoImage: Uri? = null
    private val progressDialog = CustomProgressDialog()
    var latitude = "0"
    var longitude = "0"
    var address = ""
    private var selectedProfession = ""
    private var profession = ""
    private var hospitalEmployeeUserId: String? = null
    private val myCalendar: Calendar = Calendar.getInstance()
    private var selectedTime = 1
    private var primaryVisitingHour = ""
    private var secondaryVisitingHour = ""

    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null

    // private val viewModel by viewModels<PharmacyViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        Utils.changeStatusColor(this, R.color.darkblue)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_hospital)
        binding.toolbar.toolbarTitle.text = getString(R.string.create_profile)
        binding.toolbar.ivBack.setOnClickListener { finish() }

        hospitalEmployeeUserId = ""
        if (intent != null) {
            hospitalEmployeeUserId = intent.getStringExtra("hospitalEmployeeUserId")
        }
        selectedProfession = ""
        binding.professionRG.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.doctorRB -> {
                    selectedProfession = getString(R.string.doctor)
                    profession = binding.doctorRB.text.toString()
                }
                R.id.nurseLpnRB -> {
                    selectedProfession = getString(R.string.nurseLpn)
                    profession = binding.nurseLpnRB.text.toString()
                }
                R.id.nurseNpRB -> {
                    selectedProfession = getString(R.string.nurseNp)
                    profession = binding.nurseNpRB.text.toString()
                }
                R.id.pharmacistRB -> {
                    selectedProfession = getString(R.string.pharmacist)
                    profession = binding.pharmacistRB.text.toString()
                }
                R.id.dentistRB -> {
                    selectedProfession = getString(R.string.dentist)
                    profession = binding.dentistRB.text.toString()
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
        binding.hospitalName.setText("mukesh")
        binding.mobileNumber.setText("7777777788")
        binding.services.setText("abc,abcd")
        binding.idNumberTv.setText("32332")
        binding.doctorRB.isChecked = true;
        binding.consultFee.setText("10")
        binding.fromVisitingTimeTv.text = "10:00 am"
        binding.toVisitingTimeTv.text = "05:00 pm"
        binding.fromAnotherVisitingTimeTv.text = "10:00 am"
        binding.toAnotherVisitingTimeTv.text = "06:00 pm"
        binding.saveAndRepeat.isChecked = true
        binding.createBtn.setOnClickListener {
            Log.d(
                "TAG",
                "date1 : ${binding.fromVisitingTimeTv.text}--date2 : ${binding.toVisitingTimeTv.text}"
            )

            if (binding.hospitalName.text.toString() == "") {
                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()

            } else if (binding.mobileNumber.text.toString() == "") {
                Toast.makeText(this, "Enter mobileNumber", Toast.LENGTH_SHORT).show()

            } else if (binding.services.text.toString() == "") {
                Toast.makeText(this, "Enter services", Toast.LENGTH_SHORT).show()

            } else if (selectedProfession.isEmpty()) {
                Toast.makeText(this, "Please select Profession", Toast.LENGTH_SHORT).show()
            } else if (binding.fromVisitingTimeTv.text.isEmpty() || binding.toVisitingTimeTv.text.isEmpty()) {
                Toast.makeText(this, "Please select primary hours", Toast.LENGTH_SHORT).show()
            } else if (binding.fromAnotherVisitingTimeTv.text.isEmpty() || binding.toAnotherVisitingTimeTv.text.isEmpty()) {
                Toast.makeText(this, "Please select secondary hours", Toast.LENGTH_SHORT).show()
            } else if (binding.consultFee.text.toString() == "") {
                Toast.makeText(this, "Enter consultFee", Toast.LENGTH_SHORT).show()
            } else if (binding.hospitalLocation.text.toString() == "") {
                Toast.makeText(this, "Enter hospitalLocation", Toast.LENGTH_SHORT).show()

            } else {
                primaryVisitingHour = timeDifference(
                    date2 = binding.fromVisitingTimeTv.text.toString(),
                    date1 = binding.toVisitingTimeTv.text.toString()
                )
                secondaryVisitingHour = timeDifference(
                    date2 = binding.fromAnotherVisitingTimeTv.text.toString(),
                    date1 = binding.toAnotherVisitingTimeTv.text.toString()
                )
                createAndUpdateProfile()
            }

        }
        binding.backgroundImg.setOnClickListener {
            imagePopup()
            option = bgimg
        }
        binding.logoImage.setOnClickListener {
            imagePopup()
            option = logo
        }

        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        binding.hospitalLocation.setOnClickListener {
            option = 3
            val fieldList: List<Place.Field> =
                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

            receiveData.launch(intent)
        }
    }

    private fun timeDifference(date1: String, date2: String): String {
        var diff = ""
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val date1Cov = simpleDateFormat.parse(date1)
        val date2Cov = simpleDateFormat.parse(date2)

        val difference: Long = date2Cov.time - date1Cov.time
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
        hours = if (hours < 0) -hours else hours
        Log.i("======= Hours", " :: $hours")
        diff = "$hours:$min"
        return diff
    }

    private fun imagePopup() {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.decorView.setBackgroundResource(android.R.color.transparent)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.pop_profile)
            dialog.show()
            val txtGallery = dialog.findViewById<View>(R.id.layoutGallery) as LinearLayout
            val txtCamera = dialog.findViewById<View>(R.id.layoutCamera) as LinearLayout
            txtCamera.setOnClickListener {
                val currentAPIVersion = Build.VERSION.SDK_INT
                if (currentAPIVersion >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            REQUEST_CAMERA_PERMISSION
                        )
                    } else {
                        selectCameraImage()
                        dialog.dismiss()
                    }
                } else {
                    selectCameraImage()
                    dialog.dismiss()
                }
            }
            txtGallery.setOnClickListener {
                val currentAPIVersion = Build.VERSION.SDK_INT
                if (currentAPIVersion >= Build.VERSION_CODES.M) {
                    arrayOf(
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ),
                                2
                            )
                        } else {
                            dialog.dismiss()
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            intent.type = "image/*"
//                            intent.type = "*/*";
                            intent.action = Intent.ACTION_PICK
                            startActivityForResult(
                                Intent.createChooser(intent, "Select Image"),
                                100
                            )
                        }
                    )

                } else {
                    dialog.dismiss()
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
//                    intent.type = "*/*";
                    intent.action = Intent.ACTION_PICK
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectCameraImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 200)
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            if (option == logo) {
                logoImage = imageUri
                binding.logoImage.setImageURI(imageUri)
            }
            if (option == bgimg) {
                backgroundImg = imageUri
                binding.backgroundImg.setImageURI(imageUri)
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            imageUri = getImageUri(this, imageBitmap!!)
            if (option == logo) {
                logoImage = imageUri
                binding.logoImage.setImageURI(imageUri)
            }
            if (option == bgimg) {
                backgroundImg = imageUri
                binding.backgroundImg.setImageURI(imageUri)
            }
            //binding.backgroundImg.setImageBitmap(imageBitmap)
            Log.d("TAG", "iamgedsfas:: $imageUri")
        }

    }
    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(it.data!!)
                binding.hospitalLocation.text = place.address
                latitude = place.latLng.latitude.toString()
                longitude = place.latLng.longitude.toString()
                address = (place.address)
            }
        }

    private fun createAndUpdateProfile() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        var pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        //builder.addFormDataPart("hospitalEmployeeUserId", "${hospitalEmployeeUserId}")
        builder.addFormDataPart("profession", profession)
        builder.addFormDataPart("fullName", binding.hospitalName.text.toString())
        builder.addFormDataPart("idNumber", binding.idNumberTv.text.toString())
        builder.addFormDataPart("primaryVisitingHour", primaryVisitingHour)
        builder.addFormDataPart("secondaryVisitingHour", secondaryVisitingHour)
        builder.addFormDataPart("professionType", selectedProfession)
        builder.addFormDataPart("mobileNumber", binding.hospitalName.text.toString())
        builder.addFormDataPart("services", binding.services.text.toString())
        builder.addFormDataPart("consultFees", binding.consultFee.text.toString())
        builder.addFormDataPart("location", address)
        builder.addFormDataPart("isRepeated", binding.saveAndRepeat.isChecked.toString())
        builder.addFormDataPart("latitude", latitude)
        builder.addFormDataPart("longitude", longitude)

        if (backgroundImg != null) {
            builder.addPart(Utils.multipartBodyFile(this, backgroundImg!!, "backgroundPicture"))
        } else {
            builder.addFormDataPart("backgroundPicture", "")
        }
        if (logoImage != null) {
            builder.addPart(Utils.multipartBodyFile(this, logoImage!!, "logo"))
        } else {
            builder.addFormDataPart("logo", "")
        }

        pharmacyViewModel.registerHospitalEmployeeApi(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        startActivity(Intent(this, HospitalProfile::class.java))
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

    /*
        private var progressIndicator: AlertDialog? = null
        private fun createAndUpdateProfile() {
            progressIndicator = AlertDialog.Builder(this).create()
            lifecycleScope.launchWhenStarted {
                viewModel.registerHospitalEmployee.collect { resultData ->
                    when (resultData) {
                        is PharmacyViewModel.ProcessingEvents.Loading -> {
                            Log.d("Resource_Loading", "Loading..........")
                            progressIndicator!!.showDialog(this@CreatePharmacyProfileActivity,getString(R.string.please_wait))
                        }
                        is PharmacyViewModel.ProcessingEvents.Success -> {
                            if (progressIndicator != null)
                                progressIndicator!!.dismissDialog()
                            val repositoriesModel = resultData.result as CommonResponse<Any>
                            Log.d("Resource_Success", "$repositoriesModel")

                            binding.rootLayout.snackBar(repositoriesModel.message!!)
                            startActivity(Intent(this@CreatePharmacyProfileActivity,HospitalProfile::class.java))
                            finish()
                        }
                        is PharmacyViewModel.ProcessingEvents.Failure -> {
                            val error = resultData.error
                            Log.d("Resource_Failed", " $error")
                            binding.rootLayout.snackBar(error)
                            if (progressIndicator != null)
                                progressIndicator!!.dismissDialog()
                        }
                        is PharmacyViewModel.ProcessingEvents.Error -> {
                            if (progressIndicator != null)
                                progressIndicator!!.dismissDialog()
                            val repositoriesModel = resultData.result as CommonResponse<*>
                            binding.rootLayout.snackBar(repositoriesModel.message!!)
                            Log.d(
                                "Resource_Error",
                                "${repositoriesModel.message} ${repositoriesModel.statusCode}"
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    */


}