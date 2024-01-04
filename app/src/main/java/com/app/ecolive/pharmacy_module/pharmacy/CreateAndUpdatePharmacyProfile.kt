package com.app.ecolive.pharmacy_module.pharmacy

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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCreatePharmacyBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.model.PharmacyProfileModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.util.Calendar

//@AndroidEntryPoint
class CreateAndUpdatePharmacyProfile : AppCompatActivity() {
    lateinit var binding: ActivityCreatePharmacyBinding
    private var licence = 1
    private var logo = 2
    var option = 1
    private var licenceImg: Uri? = null
    private var logoImage: Uri? = null
    private val progressDialog = CustomProgressDialog()
    var latitude = "0"
    var longitude = "0"
    var address = ""

    /*    private var hospitalEmployeeUserId: String? = null*/
    private val myCalendar: Calendar = Calendar.getInstance()
    private var selectedTime = 1
    private var primaryVisitingHour = ""
    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null

    private var profile: PharmacyProfileModel.Data? = null

    // private val viewModel by viewModels<PharmacyViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        Utils.changeStatusColor(this, R.color.darkblue)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_pharmacy)
        binding.toolbar.toolbarTitle.text = getString(R.string.create_pharmacy)
        binding.toolbar.ivBack.setOnClickListener { finish() }

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
        intent.extras?.let {
            profile =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getSerializable(
                        AppConstant.profile,
                        PharmacyProfileModel.Data::class.java
                    )
                } else {
                    it.getSerializable(AppConstant.profile) as PharmacyProfileModel.Data
                }

            binding.pharmacyName.setText(profile?.pharmacyName)
            binding.mobileNumber.setText(profile?.mobileNumber)
            binding.licenceNumberTv.setText(profile?.licenceNumber)
            binding.fromVisitingTimeTv.text = profile?.openingFrom
            binding.toVisitingTimeTv.text = profile?.openingTo
            binding.repeatForWeekCheckBox.isChecked = profile?.repeatForWeek ?: false
            binding.pharmacyLocation.text = profile?.location
            address = profile?.location.toString()
/*            latitude = doctorProfile?.latitude.toString()
            longitude = doctorProfile?.longitude.toString()*/
        }

        binding.createBtn.setOnClickListener {
            if (binding.pharmacyName.text.toString() == "") {
                Toast.makeText(this, "Enter pharmacy name", Toast.LENGTH_SHORT).show()
            } else if (binding.mobileNumber.text.toString() == "") {
                Toast.makeText(this, "Enter mobileNumber", Toast.LENGTH_SHORT).show()
            } else if (binding.fromVisitingTimeTv.text.isEmpty() || binding.toVisitingTimeTv.text.isEmpty()) {
                Toast.makeText(this, "Please select opening hours", Toast.LENGTH_SHORT).show()
            } else if (binding.pharmacyLocation.text.toString() == "") {
                Toast.makeText(this, "Enter hospitalLocation", Toast.LENGTH_SHORT).show()
            } else {
               createAndUpdateProfile()
            }
        }
        binding.licenceImg.setOnClickListener {
            imagePopup()
            option = licence
        }
        binding.logoImage.setOnClickListener {
            imagePopup()
            option = logo
        }

        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        binding.pharmacyLocation.setOnClickListener {
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

    private fun imagePopup() {
        try {
            val dialog = Dialog(this)
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
            if (option == licence) {
                licenceImg = imageUri
                binding.licenceImg.setImageURI(imageUri)
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            imageUri = getImageUri(this, imageBitmap!!)
            if (option == logo) {
                logoImage = imageUri
                binding.logoImage.setImageURI(imageUri)
            }
            if (option == licence) {
                licenceImg = imageUri
                binding.licenceImg.setImageURI(imageUri)
            }
            //binding.backgroundImg.setImageBitmap(imageBitmap)
            Log.d("TAG", "iamgedsfas:: $imageUri")
        }
    }

    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(it.data!!)
                binding.pharmacyLocation.text = place.address
                latitude = place.latLng?.latitude.toString()
                longitude = place.latLng?.longitude.toString()
                address = place.address?.toString() ?: ""
            }
        }

    private fun createAndUpdateProfile() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        /*var doctorId = ""
        intent.extras?.let {
            doctorId = it.getString(AppConstant.doctorId).toString()
        }*/

        //builder.addFormDataPart("pharmacyId", doctorId)
        builder.addFormDataPart("pharmacyName", binding.pharmacyName.text.toString())
        builder.addFormDataPart("mobileNumber", binding.mobileNumber.text.toString())
        builder.addFormDataPart("licenceNumber", binding.licenceNumberTv.text.toString())
        builder.addFormDataPart("location", address)
        builder.addFormDataPart("openingFrom", binding.fromVisitingTimeTv.text.toString())
        builder.addFormDataPart("openingTo", binding.toVisitingTimeTv.text.toString())
        builder.addFormDataPart("repeatForWeek", binding.repeatForWeekCheckBox.isChecked.toString())
        builder.addFormDataPart("latitude", latitude)
        builder.addFormDataPart("longitude", longitude)

        if (licenceImg != null) {
            builder.addPart(Utils.multipartBodyFile(this, licenceImg!!, "licenceImage"))
        } else {
            builder.addFormDataPart("licenceImage", "")
        }
        if (logoImage != null) {
            builder.addPart(Utils.multipartBodyFile(this, logoImage!!, "pharmacyImage"))
        } else {
            builder.addFormDataPart("pharmacyImage", "")
        }

        pharmacyViewModel.createAndUpdatePharmacyApi(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        val userRes = PreferenceKeeper.instance.loginResponse
                        if (userRes != null) {
                            userRes.isPharmacy = true
                        }
                        PreferenceKeeper.instance.loginResponse = userRes
                        startActivity(Intent(this, PharmacyProfile::class.java))
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

}