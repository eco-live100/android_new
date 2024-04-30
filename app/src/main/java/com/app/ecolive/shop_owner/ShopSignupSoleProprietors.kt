package com.app.ecolive.shop_owner

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View.GONE
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ShopsignupSoleproprietorsActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.ShopCategryListModel
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.nightout.ui.fragment.SelectSourceBottomSheetFragment
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class ShopSignupSoleProprietors : BaseActivity() {
    lateinit var binding: ShopsignupSoleproprietorsActivityBinding
    private val progressDialog = CustomProgressDialog()
    var cagrySubIdList = ArrayList<String>()
    lateinit var wholeCategoryList : ArrayList<ShopCategryListModel.Data>
    var selectedSubCategoryID = ""
    var cagryIdList = ArrayList<String>()
    var selectedCategoryID = ""
    private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
    var listMultipartBody: ArrayList<MultipartBody.Part> = ArrayList()
    var bodyStoreLogo: MultipartBody.Part? = null
  //  var bodyLisence: MultipartBody.Part? = null
    var mPlaceAddrs=""
    var mPlaceLat=""
    var mPlaceLang=""
    private lateinit var reqFile: RequestBody
    private var filePath: File? = null
    var isVehicalDocImageBtnClick = false
    var isstoreLogoImageBtnClick = false
    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null
    private var bgimg = 1
    private var doc1 = 2
    private var doc2 = 3
    var option = 1
    private var backgroundImg: Uri? = null
    private var docImg: Uri? = null
    private var docImg2: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(THIS!!,R.layout.shopsignup_soleproprietors_activity)
        settoolBar()
        initView()
        listMultipartBody = ArrayList()
        shopCategoriesListAPICAll()
        setSpinNoOfLoc()
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), resources.getString(R.string.google_maps_key))
        }
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?

        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME,Place.Field.ADDRESS))
        autocompleteFragment!!.setHint("Store Address")
       // (autocompleteFragment.view?.findViewById(R.id.autocomplete_fragment) as EditText).textSize = 10.0f
       // autocompleteFragment.getView()?.findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
        autocompleteFragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("ok", "Place: " + place.name + ", " + place.latLng.latitude)
                Log.i("ok", "Place: " + place.address)
                mPlaceAddrs= place.name+", "+place.address
                mPlaceLat=place.latLng.latitude.toString()
                mPlaceLang=place.latLng.longitude.toString()

            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.i("ok", "An error occurred: $status")
            }
        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if(v==binding.soleSubmitButton){
            if(isValidInput()){
                vendorShopDetailsAPICall()
            }
        }
        else if(v==binding.storeDocImage){
            isstoreLogoImageBtnClick=false
            isVehicalDocImageBtnClick = true
            option =doc1
            imagePopup()
        }
        else if(v==binding.storeDocImage2){
            if(binding.storeDocImage.getDrawable() == null){
                //we need add frontSide image first than add back side image
                MyApp.popErrorMsg("", resources.getString(R.string.plz_Upload_Verification_Document), THIS!!)

            }else {
                isstoreLogoImageBtnClick = false
                isVehicalDocImageBtnClick = false
                option =doc2
                imagePopup()
            }
        }
        else if(v==binding.storeLogoImage){
            isstoreLogoImageBtnClick=true
            option=bgimg
            imagePopup()
        }
    }

    private fun vendorShopDetailsAPICall() {
        try {
            MyApp.hideSoftKeyboard(THIS!!)
        } catch (e: Exception) {
        }
      progressDialog.show(THIS!!)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("shopCategoryId",selectedCategoryID)
        builder.addFormDataPart("shopSubCategoryId",selectedSubCategoryID)
        builder.addFormDataPart("shopName",binding.soleStorName.text.toString())
        builder.addFormDataPart("shopType",intent.getStringExtra(AppConstant.STORE_TYPE)!!)
        builder.addFormDataPart("firstName",binding.soleFName.text.toString())
        builder.addFormDataPart("lastName",binding.soleLName.text.toString())
        builder.addFormDataPart("email",binding.soleEmail.text.toString())
        builder.addFormDataPart("countryCode",binding.forgotContryPicker.selectedCountryCode.toString())
        builder.addFormDataPart("mobileNumber",binding.soleMobileNo.text.toString())
        builder.addFormDataPart("numberOfLocation",binding.spinLocation.selectedItem.toString())
        builder.addFormDataPart("latitude",mPlaceLat)
        builder.addFormDataPart("longitude",mPlaceLang)
        builder.addFormDataPart("storeAddress",mPlaceAddrs)
        builder.addFormDataPart("shopDescription",binding.soleStoreDescription.text.toString())
        //storeLogo
        if (bodyStoreLogo != null) {
            builder.addPart(bodyStoreLogo!!)
        }
        //else {
         //   builder.addFormDataPart("storeLogo", "")
      //  }
       //addDoc
        for (i in 0 until listMultipartBody.size){
            builder.addPart(listMultipartBody[i])
        }

      var vendrShopViewModel= CommonViewModel(THIS!!)
        vendrShopViewModel.uploadShopSignup(builder.build()).observe(THIS!!) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var mdol= PreferenceKeeper.instance.loginResponse
                        if (mdol != null) {
                            mdol.isVendor=true
                        }
                        PreferenceKeeper.instance.loginResponse=mdol
                        Utils.showMessage(THIS!!, it.message!!)
                        val i = Intent(this, ShopOwnerHomePageNavigationActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                        finish()
                    }
                }
                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("",it.message!!,this@ShopSignupSoleProprietors)

                }
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


            imageUri = data.data
            imageCreaterForApi(imageUri,data)
            if (option == doc1) {
                docImg = imageUri
                binding.storeDocImage.setImageURI(imageUri)
            }
            if (option == doc2) {
                docImg2 = imageUri
                binding.storeDocImage2.setImageURI(imageUri)
            }
            if (option == bgimg) {
                backgroundImg = imageUri
                binding.storeLogoImage.setImageURI(imageUri)
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?


            imageUri = getImageUri(this, imageBitmap!!)
            imageCreaterForApi(imageUri, data)
            if (option == doc1) {
                docImg = imageUri
                binding.storeDocImage.setImageURI(imageUri)
            }
            if (option == doc2) {
                docImg2 = imageUri
                binding.storeDocImage2.setImageURI(imageUri)
            }
            if (option == bgimg) {
                backgroundImg = imageUri
                binding.storeLogoImage.setImageURI(imageUri)
            }
            //binding.backgroundImg.setImageBitmap(imageBitmap)
            Log.d("TAG", "iamgedsfas:: $imageUri")
        }
    }


    private fun setSpinNoOfLoc() {
        var list = ArrayList<String>()
        list.add(resources.getString(R.string.select_noofloc))
        for (i in 1 until 10){
            list.add(i.toString())
        }
        val aa: ArrayAdapter<Any> = ArrayAdapter<Any>(this, android.R.layout.simple_spinner_item, list as List<Any>)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinLocation.adapter = aa
        binding.spinLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }



    private fun isValidInput(): Boolean {

        if(binding.soleStorName.text.toString().isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_storeName),THIS!!)
            return false
        }

       else  if(mPlaceAddrs.isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_storeAddrs),THIS!!)
            return false
        }

        else  if(binding.soleFName.text.toString().isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_fname),THIS!!)
            return false
        }
        else  if(binding.soleLName.text.toString().isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_surname),THIS!!)
            return false
        }
        else  if(binding.soleEmail.text.toString().isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_email),THIS!!)
            return false
        }
        else if (!MyApp.isValidEmail(binding.soleEmail.text.toString())) {
            MyApp.popErrorMsg("", resources.getString(R.string.email_notvalid), THIS!!)
            return false

        }

        else  if(binding.soleMobileNo.text.toString().isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_mobno),THIS!!)
            return false
        }
        else if(!binding.soleIAgreeChkbox.isChecked){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_iAgree),THIS!!)
            return false
        }
        else if(binding.spinCategory.selectedItem.toString() == resources.getString(R.string.plz_select_catgry)){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_select_catgry),THIS!!)
            return false
        }
        else if(binding.spinLocation.selectedItem.toString() == resources.getString(R.string.select_noofloc)){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_select_nooflocation),THIS!!)
            return false
        }
      /*  else if(binding.spinSubCategory.selectedItem.toString() == resources.getString(R.string.plz_select_subcatgry)){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_select_subcatgry),THIS!!)
            return false
        }*/
        else if (binding.storeDocImage.getDrawable() == null) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_Upload_Verification_Document), THIS!!)
            return false
        }

        else if (binding.storeDocImage2.getDrawable() == null) {
            MyApp.popErrorMsg("", resources.getString(R.string.plz_Upload_Verification_Document_back), THIS!!)
            return false
        }
        return true
    }

    private fun initView() {
         setTouchNClick(binding.soleSubmitButton)
         setTouchNClick(binding.storeDocImage2)
         setTouchNClick(binding.storeDocImage)
         setTouchNClick(binding.storeLogoImage)

    }

    @SuppressLint("SetTextI18n")
    private fun settoolBar() {
        Utils.changeStatusTextColor2(this)
        binding.toolbarShopSole.toolbarTitle.text = "Get Started"
        binding.toolbarShopSole.ivBack.setOnClickListener { finish() }
    }

    private fun shopCategoriesListAPICAll() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        loginViewModel.shopCategoriesList().observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        wholeCategoryList = ArrayList ()
                        wholeCategoryList.addAll(it.data)
                        setCagrySpin(wholeCategoryList)


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

    private fun setCagrySpin(list: List<ShopCategryListModel.Data>) {
        var cagryNameList = ArrayList<String>()
        cagryNameList.add(resources.getString(R.string.plz_select_catgry))
        cagryIdList = ArrayList<String>()
        cagryIdList.add("00000")
        for (i in 0 until list.size) {
            cagryNameList.add(list[i].categoryName)
            cagryIdList.add(list[i]._id)

        }

        val aa: ArrayAdapter<Any> = ArrayAdapter<Any>(this, android.R.layout.simple_spinner_item, cagryNameList as List<Any>)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinCategory.adapter = aa

        binding.spinCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategoryID = cagryIdList[position]
                    if(position>0) {
                        var subCaglist = wholeCategoryList[position-1].subCategories
                        if (subCaglist.isNotEmpty()) {
                            setSubCatgrySpin(subCaglist)
                        }else{
                            setSubCatgrySpin(ArrayList<ShopCategryListModel.SubCategory>())
                        }
                    }else{
                        setSubCatgrySpin(ArrayList<ShopCategryListModel.SubCategory>())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    private fun setSubCatgrySpin(subCaglist: List<ShopCategryListModel.SubCategory>) {
        var listSubCtgryName=ArrayList<String>()
        cagrySubIdList = ArrayList<String>()
        listSubCtgryName.add(resources.getString(R.string.plz_select_subcatgry))
        cagrySubIdList.add("000000")
        for (i in 0 until subCaglist.size){
            listSubCtgryName.add(subCaglist[i].subCategoryName)
            cagrySubIdList.add(subCaglist[i]._id)
        }
        val aa: ArrayAdapter<Any> = ArrayAdapter<Any>(this, android.R.layout.simple_spinner_item, listSubCtgryName as List<Any>)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinSubCategory.adapter = aa

        binding.spinSubCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSubCategoryID = cagrySubIdList[position]


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }




    private fun setBody(imageUri: Uri, flag: String) {

        val filePath = getFilePath(this, imageUri)
        this.filePath = File(filePath!!)

        reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), this.filePath!!)
        if (isVehicalDocImageBtnClick) {
            var body = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)
            if(listMultipartBody.size>0){
                listMultipartBody[0] = body// set(replace) 0th postion
            }else{
                listMultipartBody.add(0,body)// add 0th postion
            }

        }
         else {
            var bodyLisence = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)
            if(listMultipartBody.size==2){
                listMultipartBody[1] = bodyLisence//set 1st postion
            }else{
                listMultipartBody.add(1,bodyLisence)//add 1st postion
            }

        }

    }

    private fun setBodyStorLogo(imageUri: Uri, flag: String) {
        val filePath = getFilePath(this, imageUri!!)
        this.filePath = File(filePath)

        reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), this.filePath!!)
        bodyStoreLogo = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)

    }

    fun imageCreaterForApi(imageUri: Uri?, data: Intent) {


        //imageUrl = Uri.parse(selectedMedia[0].path)
        if(isstoreLogoImageBtnClick){
            try {
                binding.storeLogoImage.setImageBitmap(null)
                binding.storeLogoImage.setImageURI(imageUri)
                binding.storeLogoConstrentInner.visibility=GONE
            } catch (e: Exception) {
                Log.d("crashImage", "onActivityResult: $e")
            }

            setBodyStorLogo(imageUri!!, "storeLogo")
        }
        else{

            if (isVehicalDocImageBtnClick) {
                try {
                    binding.storeDocImage.setImageBitmap(null)
                    binding.storeLogoImage.setImageURI(imageUri)
                    binding.vehicalDocConstrent.visibility=GONE
                } catch (e: Exception) {
                    Log.d("crashImage", "onActivityResult: $e")
                }

                setBody(imageUri!!, "storeDocument")
            } else {
                try {
                    binding.storeDocImage2.setImageBitmap(null)
                    binding.storeLogoImage.setImageURI(imageUri)
                    binding.vehicalDocConstrent2.visibility=GONE
                } catch (e: Exception) {
                    Log.d("crashImage", "onActivityResult: $e")
                }
                setBody(imageUri!!, "storeDocument")

            }
        }


    }
}