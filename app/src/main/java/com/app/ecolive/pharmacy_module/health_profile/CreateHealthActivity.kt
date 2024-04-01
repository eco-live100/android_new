package com.app.ecolive.pharmacy_module.health_profile

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCreateHealthBinding
import com.app.ecolive.pharmacy_module.PharmacyStepActivity
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.SearchMedicineListAdapter
import com.app.ecolive.pharmacy_module.model.HealthProfileData
import com.app.ecolive.pharmacy_module.model.SearchMedicineListData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.ByteArrayOutputStream

class CreateHealthActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateHealthBinding

    private val progressDialog = CustomProgressDialog()
    lateinit var searchMedicineListAdapter: SearchMedicineListAdapter
    private var searchMedicineList: ArrayList<SearchMedicineListData> = ArrayList()
    var healthProfileData: HealthProfileData? = null

    lateinit var medicineListAdapter: SearchMedicineListAdapter
    val flexboxSearchLayoutManager = FlexboxLayoutManager(this)
    val flexboxLayoutManager = FlexboxLayoutManager(this)
    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null

    companion object {
        var mInstance: CreateHealthActivity? = null
        var selectedMedicineList: ArrayList<SearchMedicineListData> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.darkblue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_health)
        binding.toolbar.toolbarTitle.text = "Profile"
        binding.toolbar.help.visibility = View.VISIBLE
        mInstance = this
        initData()
        selectedMedicineList.clear()
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

    }

    fun initData() {
        intent.extras?.let {
            healthProfileData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(
                    AppConstant.data,
                    HealthProfileData::class.java
                )
            } else {
                it.getSerializable(AppConstant.data) as HealthProfileData
            }
            Log.d("TAG", "initData ${healthProfileData}==${healthProfileData?.name}")
            binding.userName.setText(healthProfileData?.name)
            binding.address.setText(healthProfileData?.address)
            binding.ssnNumber.setText(healthProfileData?.ssn)
            Glide.with(this).load("${AppConstant.BASE_URL_Image}${healthProfileData?.insurance}")
                .placeholder(R.drawable.bg_dash).centerCrop()
                .into(binding.insuranceImg)

        }
        binding.storeLogoConstrentInner.setOnClickListener {
            imagePopup()
        }

        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        medicineListAdapter =
            SearchMedicineListAdapter(this, selectedMedicineList)
        binding.medicineRecyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = medicineListAdapter
        }

        flexboxSearchLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        searchMedicineListAdapter =
            SearchMedicineListAdapter(this, searchMedicineList)
        binding.searchMedicineRecyclerView.apply {
            layoutManager = flexboxSearchLayoutManager
            adapter = searchMedicineListAdapter
        }

        binding.clearIv.visibility = View.GONE
        binding.clearIv.setOnClickListener { binding.searchEt.text.clear() }
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.searchEt.text.isEmpty()) {
                    binding.clearIv.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    searchMedicineList.clear()
                    searchMedicineListAdapter.notifyDataSetChanged()
                } else {
                    binding.clearIv.visibility = View.VISIBLE
                }
                if (binding.searchEt.text.isNotEmpty() && binding.searchEt.text.length >= 3)
                    searchMedication(binding.searchEt.text.toString())
            }
        })

        binding.nextButton.setOnClickListener {
            if (binding.userName.text.toString().trim() != "") {
                if (binding.address.text.toString().trim() != "") {
                    if (binding.ssnNumber.text.toString().trim() != "") {
                        createHealthApi()
                    } else {
                        Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter SSN number", Toast.LENGTH_SHORT).show()
            }
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
            val imageUri = data.data
            binding.insuranceImg.setImageURI(imageUri)
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            val imageUri = getImageUri(this, imageBitmap!!)
            binding.insuranceImg.setImageURI(imageUri)
        }
    }

    private fun searchMedication(searchText: String) {
        val pharmacyViewModel = PharmacyViewModel(this)
        //progressDialog.show(this)
        pharmacyViewModel.searchMedicineApi(search = searchText).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { data ->
                        searchMedicineList.clear()
                        searchMedicineList.addAll(data.data)
                        searchMedicineListAdapter.notifyDataSetChanged()
                    }
                }

                Status.LOADING -> {
                    Timber.d("LOADING: ")
                }

                Status.ERROR -> {
                    //progressDialog.dialog.dismiss()
                    Timber.d("ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    fun getInstance(): CreateHealthActivity? {
        return mInstance
    }

    fun selectMedicine(item: SearchMedicineListData) {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        if (selectedMedicineList.contains(item)) selectedMedicineList.remove(item)
        else selectedMedicineList.add(item)
        medicineListAdapter.notifyDataSetChanged()
    }


    private fun createHealthApi() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("name", (binding.userName.text ?: "").toString())
        builder.addFormDataPart("address", (binding.address.text ?: "").toString())
        builder.addFormDataPart("ssn", (binding.ssnNumber.text ?: "").toString())
        builder.addFormDataPart(
            "medications", selectedMedicineList.toString()
        )
        if (imageUri != null) {
            builder.addPart(Utils.multipartBodyFile(this, imageUri!!, "images"))
        } else {
            builder.addFormDataPart("images", "")
        }
        pharmacyViewModel.createHealthProfile(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        PreferenceKeeper.instance.isHealthProfileCreate = true
                        PharmacyProcessActivity.isUpdateProfile = false
                        startActivity(
                            Intent(this, PharmacyStepActivity::class.java)
                        )
                        finish()
                    }
                }

                Status.LOADING -> {
                    Timber.d("LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Timber.d("ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

}