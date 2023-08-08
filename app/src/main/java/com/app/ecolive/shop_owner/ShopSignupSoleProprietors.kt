package com.app.ecolive.shop_owner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ShopsignupSoleproprietorsActivityBinding
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.ShopCategryListModel
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.nightout.ui.fragment.SelectSourceBottomSheetFragment
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


class ShopSignupSoleProprietors : BaseActivity(), OnSelectOptionListener {
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
            onSelectImage()
        }
        else if(v==binding.storeDocImage2){
            if(binding.storeDocImage.getDrawable() == null){
                //we need add frontSide image first than add back side image
                MyApp.popErrorMsg("", resources.getString(R.string.plz_Upload_Verification_Document), THIS!!)

            }else {
                isstoreLogoImageBtnClick = false
                isVehicalDocImageBtnClick = false
                onSelectImage()
            }
        }
        else if(v==binding.storeLogoImage){
            isstoreLogoImageBtnClick=true
            onSelectImage()
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


    private fun onSelectImage() {
        if (!Utils.checkingPermissionIsEnabledOrNot(THIS!!)) {
            Utils.requestMultiplePermission(THIS!!, VehicleInfoActivity.requestPermissionCode)
        } else {
            selectSourceBottomSheetFragment = SelectSourceBottomSheetFragment(this, "")
            selectSourceBottomSheetFragment.show(
                THIS!!.supportFragmentManager,
                "selectSourceBottomSheetFragment"
            )
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
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (!selectedMedia.isNullOrEmpty()) {
                    val bitmap: Bitmap?
                    bitmap = BitmapFactory.decodeFile(selectedMedia[0].path)
                    //imageUrl = Uri.parse(selectedMedia[0].path)
                    if(isstoreLogoImageBtnClick){
                        try {
                            binding.storeLogoImage.setImageBitmap(null)
                            binding.storeLogoImage.setImageBitmap(bitmap)
                            binding.storeLogoConstrentInner.visibility=GONE
                        } catch (e: Exception) {
                            Log.d("crashImage", "onActivityResult: $e")
                        }

                        setBodyStorLogo(bitmap!!, "storeLogo")
                    }
                    else{

                        if (isVehicalDocImageBtnClick) {
                            try {
                                binding.storeDocImage.setImageBitmap(null)
                                binding.storeDocImage.setImageBitmap(bitmap)
                                binding.vehicalDocConstrent.visibility=GONE
                            } catch (e: Exception) {
                                Log.d("crashImage", "onActivityResult: $e")
                            }

                            setBody(bitmap!!, "storeDocument")
                        } else {
                            try {
                                binding.storeDocImage2.setImageBitmap(null)
                                binding.storeDocImage2.setImageBitmap(bitmap)
                                binding.vehicalDocConstrent2.visibility=GONE
                            } catch (e: Exception) {
                                Log.d("crashImage", "onActivityResult: $e")
                            }
                            setBody(bitmap!!, "storeDocument")

                        }
                    }


                }
            }
        }

    private fun setBody(bitmap: Bitmap, flag: String) {
        val filePath = Utils.saveImage(THIS!!, bitmap)
        this.filePath = File(filePath)

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

    private fun setBodyStorLogo(bitmap: Bitmap, flag: String) {
        val filePath = Utils.saveImage(THIS!!, bitmap)
        this.filePath = File(filePath)

        reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), this.filePath!!)
        bodyStoreLogo = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)

    }
}