package com.app.ecolive.pharmacy_module.pharmacy

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAddMedicineBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.model.DoctorProfileModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//@AndroidEntryPoint
class AddMedicine : AppCompatActivity() {
    lateinit var binding: ActivityAddMedicineBinding
    private var licence = 1
    var option = 1
    private var licenceImg: Uri? = null
    private val progressDialog = CustomProgressDialog()

    private val myCalendar: Calendar = Calendar.getInstance()
    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null

    private var doctorProfile: DoctorProfileModel.Data? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        Utils.changeStatusColor(this, R.color.darkblue)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_medicine)
        binding.toolbar.toolbarTitle.text = getString(R.string.add_medicine)
        binding.toolbar.ivBack.setOnClickListener { finish() }

        val date =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                val date = myCalendar.time
                val formatter =
                    SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH) //or use getDateInstance()
                val formatedDate = formatter.format(date)
                binding.fromDate.text = formatedDate

            }
        /*        intent.extras?.let {
                    doctorProfile =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.getSerializable(
                                AppConstant.doctorProfile,
                                DoctorProfileModel.Data::class.java
                            )
                        } else {
                            it.getSerializable(AppConstant.trackOrderDetail) as DoctorProfileModel.Data
                        }
                    binding.pharmacyName.setText(doctorProfile?.fullName)
                    binding.mobileNumber.setText(doctorProfile?.mobileNumber)
                    binding.licenceNumberTv.setText(doctorProfile?.idNumber)
                    binding.fromVisitingTimeTv.text = "10:00 am"
                    binding.toVisitingTimeTv.text = "05:00 pm"
                    binding.repeatForWeekCheckBox.isChecked = doctorProfile?.isRepeated ?: false
                    binding.pharmacyLocation.text = doctorProfile?.location
                    latitude = doctorProfile?.latitude.toString()
                    longitude = doctorProfile?.longitude.toString()
                }*/
        binding.calender1.setOnClickListener {
            val dialog = DatePickerDialog(
                this,
                R.style.my_dialog_theme,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        binding.createBtn.setOnClickListener {
            if (binding.nameEt.text.toString() == "") {
                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
            } else if (binding.quantityEt.text.toString() == "") {
                Toast.makeText(this, "Enter quantity", Toast.LENGTH_SHORT).show()
            } else if (binding.priceEt.text.toString() == "") {
                Toast.makeText(this, "Enter price", Toast.LENGTH_SHORT).show()
            } else if (binding.fromDate.text.toString() == "") {
                Toast.makeText(this, "Please select expire date", Toast.LENGTH_SHORT).show()
            } else if (binding.descriptionEt.text.toString() == "") {
                Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
            } else if (binding.precautionsEt.text.toString() == "") {
                Toast.makeText(this, "Please enter precautions", Toast.LENGTH_SHORT).show()
            } else {
                addMedicine()
            }
        }
        binding.licenceImg.setOnClickListener {
            imagePopup()
            option = licence
        }
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
            licenceImg = imageUri
            binding.licenceImg.setImageURI(imageUri)

        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            imageUri = getImageUri(this, imageBitmap!!)
            licenceImg = imageUri
            binding.licenceImg.setImageURI(imageUri)
            //binding.backgroundImg.setImageBitmap(imageBitmap)
            Log.d("TAG", "iamgedsfas:: $imageUri")
        }
    }

    private fun addMedicine() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        var pharmacyId = ""
        intent.extras?.let {
            pharmacyId = it.getString(AppConstant.pharmacyId).toString()
        }

        builder.addFormDataPart("pharmacyId", pharmacyId)
        builder.addFormDataPart("medicineName", binding.nameEt.text.toString())
        builder.addFormDataPart("quantity", binding.quantityEt.text.toString())
        builder.addFormDataPart("price", binding.priceEt.text.toString())
        builder.addFormDataPart("expireDate", binding.fromDate.text.toString())
        builder.addFormDataPart("description", binding.descriptionEt.text.toString())
        builder.addFormDataPart("precautions", binding.precautionsEt.text.toString())
        if (licenceImg != null) {
            builder.addPart(Utils.multipartBodyFile(this, licenceImg!!, "image"))
        } else {
            builder.addFormDataPart("image", "")
        }

        pharmacyViewModel.addMedicineApi(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
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