package com.app.ecolive.rider_module

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
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityVehicleInfoBinding
import com.app.ecolive.rider_module.adapter.CstmVehleCatgryAdapter
import com.app.ecolive.rider_module.model.VehicalCatgryListModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.getFilePath
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class VehicleInfoActivity : BaseActivity() {
    lateinit var binding: ActivityVehicleInfoBinding
    private val progressDialog = CustomProgressDialog()
    /* private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
   var body: MultipartBody.Part? = null
    var bodyLisence: MultipartBody.Part? = null
    private lateinit var reqFile: RequestBody
    private var filePath: File? = null*/
    //var isVehicalDocImageBtnClick = false

    lateinit var vehicleCatgryListID: ArrayList<String>
    var selectedVehicleTypeName = ""
    var selectedVehicleTypeID = ""
    var selectedVehicleCategoryID = ""

    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null
    private var vehicleDocUri : Uri? = null
    private var dlUri : Uri? = null
    var vehicleDoc = 1
    var dl = 2
    var option = 1

    companion object {
        const val requestPermissionCode = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_info)
        initView()
        vehicleCategoriesListAPICall()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        MyApp.preventDoubleClick(v!!)
        if (v == binding.vehicalSignupBtn) {
            if (isValidInput()) {
                riderVehicleDetailsAPICall(false)
            }
        } else if (v == binding.vehicalDocImage) {
            //isVehicalDocImageBtnClick = true
            option = vehicleDoc
            imagePopup()
        } else if (v == binding.vehicalLicenseImage) {
            //isVehicalDocImageBtnClick = false
            option = dl
            imagePopup()
        } else if (v == binding.vehicalSignupBtnAnother) {
            if (isValidInput()) {
                riderVehicleDetailsAPICall(true)
            }
        } else if (v == binding.vehicalBackBtn) {
            finish()
        }
    }
    private fun multipartBodyFile(imageUri : Uri,requestParam : String) : MultipartBody.Part {
        val filePath = getFilePath(this, imageUri)
        val file = File(filePath)
        val reqFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(requestParam, file.name, reqFile)
    }
    private fun riderVehicleDetailsAPICall(wannaAddMore: Boolean) {
        try {
            MyApp.hideSoftKeyboard(THIS!!)
        } catch (e: Exception) {
        }
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("vehicleCategoryId", selectedVehicleCategoryID)
        builder.addFormDataPart("vehicleTypeId", selectedVehicleTypeID)
        builder.addFormDataPart("vehicleName", binding.vehicalName.text.toString())
        builder.addFormDataPart("vehicleNumber", binding.vehicalNumber.text.toString())

        vehicleDocUri?.let {
            builder.addPart(multipartBodyFile(it,"vehicleDocument").body)
        }
        dlUri?.let {
            builder.addPart(multipartBodyFile(it,"driverLicense").body)
        }
       /* if (vehicleDocUri != null) {
            builder.addPart(multipartBodyFile(vehicleDocUri,"vehicleDocument").body)
        } else {
            builder.addFormDataPart("vehicleDocument", "")
        }
        if (dlUri != null) {
            builder.addPart(multipartBodyFile)
        } else {
            builder.addFormDataPart("driverLicense", "")
        }*/
        progressDialog.show(THIS!!)
        val vehicleViewModel = CommonViewModel(THIS!!)
        vehicleViewModel.updateVehicleProfile(builder.build()).observe(THIS!!) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        Utils.showMessage(THIS!!, it.message!!)
                        var mdol = PreferenceKeeper.instance.loginResponse
                        if (mdol != null) {
                            mdol.isRider = true
                        }
                        PreferenceKeeper.instance.loginResponse = mdol
                        if (wannaAddMore) {
                            //reset all the data
                            binding.spinVehicleType.setSelection(0)
                            binding.spintVehiclecatgry3.setSelection(0)
                            binding.vehicalName.setText("")
                            binding.vehicalNumber.setText("")
                            binding.vehicalDocImage.setImageResource(0)
                            binding.vehicalLicenseImage.setImageResource(0)

                        } else {
                            if (intent.getBooleanExtra(
                                    AppConstant.IS_FROM_USERTYPEOPTIONACTIVITY,
                                    false
                                )
                            ) {
                                startActivity(
                                    Intent(
                                        THIS,
                                        UserHomePageNavigationActivity::class.java
                                    )
                                )
                            }
                            finish()
                        }

                    }
                }
                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    Utils.showMessage(THIS!!, it.message!!)
                }
            }
        }
    }

    lateinit var ctgryTypeList: ArrayList<VehicalCatgryListModel.VehicleType>
    private fun vehicleCategoriesListAPICall() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)

        loginViewModel.vehicleCategoriesList().observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        // setSpinVehicalCatgry(it.data.vehicleType)
                        //setCategry
                        setSpinVehicalCatgry(it.data.vehicleCategory)
                        //setType
                        ctgryTypeList = ArrayList()
                        var vType = VehicalCatgryListModel.VehicleType(
                            "",
                            resources.getString(R.string.select_vehiclecagry),
                            "#333333"
                        )
                        ctgryTypeList = it.data.vehicleType
                        ctgryTypeList.add(0, vType)
                        setSpinVehicalType(ctgryTypeList)//new 2

                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, THIS!!)
                }
            }
        }
    }

    lateinit var cstmVehleCatgryAdapter: CstmVehleCatgryAdapter
    private fun setSpinVehicalType(vehicleCategory: ArrayList<VehicalCatgryListModel.VehicleType>) {
        cstmVehleCatgryAdapter = CstmVehleCatgryAdapter(this@VehicleInfoActivity, vehicleCategory)
        binding.spintVehiclecatgry3.adapter = cstmVehleCatgryAdapter
        binding.spintVehiclecatgry3.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    ///  selectedVehicleTypeID= vehicleCategory[position]._id
                    selectedVehicleTypeID = vehicleCategory[position]._id
                    selectedVehicleTypeName = vehicleCategory[position].title
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }


    private fun setSpinVehicalCatgry(vehicleTypeList: ArrayList<VehicalCatgryListModel.VehicleCategory>) {
        var listtype = ArrayList<String>()
        vehicleCatgryListID = ArrayList()
        listtype.add(resources.getString(R.string.plz_selectvehicleCategory))
        vehicleCatgryListID.add("000")//for dummy
        for (i in 0 until vehicleTypeList.size) {
            listtype.add(vehicleTypeList[i].title)
            vehicleCatgryListID.add(vehicleTypeList[i]._id)
        }

        val aa: ArrayAdapter<Any> =
            ArrayAdapter<Any>(this, android.R.layout.simple_spinner_item, listtype as List<Any>)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinVehicleType.adapter = aa

        binding.spinVehicleType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedVehicleCategoryID = vehicleCatgryListID[position]
                    if (binding.spinVehicleType.selectedItem.toString()
                            .equals("motorcycle", ignoreCase = true)
                    ) {
                        binding.vehicalImageLogo.setImageResource(R.drawable.ic_bikelogo)
                    } else
                        binding.vehicalImageLogo.setImageResource(R.drawable.ic_carlogo)


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

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
            txtGallery.setOnClickListener { v: View? ->
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
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            if(option==vehicleDoc){
                vehicleDocUri = imageUri
                binding.vehicalDocImage.setImageURI(vehicleDocUri)
            }
            if(option==dl){
                dlUri = imageUri
                binding.vehicalLicenseImage.setImageURI(dlUri)
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            imageUri = getImageUri(this, imageBitmap!!)
            if(option==vehicleDoc){
                vehicleDocUri = imageUri
                binding.vehicalDocImage.setImageURI(vehicleDocUri)
            }
            if(option==dl){
                dlUri = imageUri
                binding.vehicalLicenseImage.setImageURI(dlUri)
            }
            Log.d("TAG", "iamgedsfas:: $imageUri")
        }

    }

/*    private fun onSelectImage() {
        if (!Utils.checkingPermissionIsEnabledOrNot(THIS!!)) {
            Utils.requestMultiplePermission(THIS!!, requestPermissionCode)
        } else {
            selectSourceBottomSheetFragment = SelectSourceBottomSheetFragment(this, "")
            selectSourceBottomSheetFragment.show(
                THIS!!.supportFragmentManager,
                "selectSourceBottomSheetFragment"
            )
        }
    }*/

/*    private fun onSelectImageLicense() {
        if (!Utils.checkingPermissionIsEnabledOrNot(THIS!!)) {
            Utils.requestMultiplePermission(THIS!!, requestPermissionCode)
        } else {
            selectSourceBottomSheetFragment = SelectSourceBottomSheetFragment(this, "")
            selectSourceBottomSheetFragment.show(
                THIS!!.supportFragmentManager,
                "selectSourceBottomSheetFragment"
            )
        }
    }*/

    private fun isValidInput(): Boolean {
        if (binding.spinVehicleType.selectedItem.toString() == resources.getString(R.string.plz_selectvehicleCategory)) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_selectvehicleCategory), THIS!!)
            return false
        } else if (selectedVehicleTypeName == resources.getString(R.string.select_vehiclecagry)) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_selectvehicleType), THIS!!)
            return false
        } else if (binding.vehicalName.text.toString().isBlank()) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_enterVehicleName), THIS!!)
            return false
        } else if (binding.vehicalNumber.text.toString().isBlank()) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_enterVehicleNumber), THIS!!)
            return false
        } else if (binding.vehicalDocImage.getDrawable() == null) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_Upload_Vehicle_Document), THIS!!)
            return false
        } else if (binding.vehicalLicenseImage.getDrawable() == null) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_Upload_Vehicle_license), THIS!!)
            return false
        }
        return true
    }


    private fun initView() {
        Utils.changeStatusColor(this, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(this)
        binding.vehicalDocImage.setOnClickListener(this)
        binding.vehicalLicenseImage.setOnClickListener(this)
        setTouchNClick(binding.vehicalSignupBtn)
        setTouchNClick(binding.vehicalSignupBtnAnother)
        setTouchNClick(binding.vehicalBackBtn)


    }

/*    override fun onOptionSelect(option: String) {
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
    }*/

/*
    private val receiveData = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (!selectedMedia.isNullOrEmpty()) {
                    val bitmap: Bitmap?
                    bitmap = BitmapFactory.decodeFile(selectedMedia[0].path)
                    //imageUrl = Uri.parse(selectedMedia[0].path)
                    if (isVehicalDocImageBtnClick) {
                        try {
                            binding.vehicalDocImage.setImageBitmap(null)
                            binding.vehicalDocImage.setImageBitmap(bitmap)
                            binding.vehicalDocConstrent.visibility = INVISIBLE
                        } catch (e: Exception) {
                            Log.d("crashImage", "onActivityResult: " + e)
                        }

                        setBody(bitmap!!, "vehicleDocument")
                    } else {
                        try {
                            binding.vehicalLicenseImage.setImageBitmap(null)
                            binding.vehicalLicenseImage.setImageBitmap(bitmap)
                            binding.vehicalLicenseConstrent.visibility = INVISIBLE
                        } catch (e: Exception) {
                            Log.d("crashImage", "onActivityResult: " + e)
                        }
                        setBody(bitmap!!, "driverLicense")

                    }
                }
            }
        }
*/

/*    private fun setBody(bitmap: Bitmap, flag: String) {
        val filePath = Utils.saveImage(THIS!!, bitmap)
        this.filePath = File(filePath)
        reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), this.filePath!!)
        if (isVehicalDocImageBtnClick)
            body = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)
        else
            bodyLisence = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)

    }*/
}