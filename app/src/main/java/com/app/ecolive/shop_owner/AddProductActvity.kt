package com.app.ecolive.shop_owner


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.AddproductActivityBinding
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.adapters.ColorCodeAdapter
import com.app.ecolive.shop_owner.adapters.ImageCaptureListAdapter
import com.app.ecolive.shop_owner.model.*
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.mrudultora.colorpicker.ColorPickerPopUp
import com.mrudultora.colorpicker.ColorPickerPopUp.OnPickColorListener
import com.nightout.ui.fragment.SelectSourceBottomSheetFragment
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class AddProductActvity : BaseActivity(), OnSelectOptionListener,OnItemSelectedListener {
    lateinit var binding: AddproductActivityBinding

    var clrList = ArrayList<ColorModel>()
    private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
    lateinit var imageCaptureListAdapter: ImageCaptureListAdapter
    var listImage = ArrayList<ImageCaptureListModel>()
    lateinit var colorCodeAdapter: ColorCodeAdapter
    private val progressDialog = CustomProgressDialog()

    var imagePath = ""

    lateinit var storeData: ShopListModel.Data

    lateinit var fontRobotoRegular: Typeface
      var adapterBrand :ArrayAdapter<String> ?=null
      var adapterSize :ArrayAdapter<String> ?=null
      var adapterproductfor :ArrayAdapter<String> ?=null
    var colorKeySave = "color"
    var attributeList = kotlin.collections.ArrayList<AttributeModel.Data>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@AddProductActvity, R.layout.addproduct_activity)
        initView()
        setToolBar()
        setListColor()
        setImageList()
        //  runTimeSpinner()
        getDataIntent()
        // productAttributeFormAPICall()
        getAttributeApi()

    }

    private fun getDataIntent() {
        storeData = intent.getSerializableExtra(AppConstant.STORE_DATA) as ShopListModel.Data
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v == binding.addProductClrPicker) {
            val colorPickerPopUp = ColorPickerPopUp(this@AddProductActvity) // Pass the context.
            colorPickerPopUp.setShowAlpha(true) // By default show alpha is true.
                .setDefaultColor(Color.BLUE) // By default red color is set.
                .setDialogTitle("Pick a Color")
                .setOnPickColorListener(object : OnPickColorListener {
                    override fun onColorPicked(color: Int) {
                        Log.d("ok", "onColorPicked: " + color)
                        val hexColor = String.format("#%06X", 0xFFFFFF and color)
                        Log.d("ok", "hexColor " + hexColor)
                        binding.addProductColorCodeEdit.setText(hexColor)
                    }

                    override fun onCancel() {
                        colorPickerPopUp.dismissDialog() // Dismiss the dialog.
                    }
                })
                .show()

        } else if (v == binding.addProductClrAdd) {
            if (binding.addProductColorCodeEdit.text.toString().isNotBlank()) {
                var clrModel = ColorModel(binding.addProductColorCodeEdit.text.toString())
                clrList.add(clrModel)
                colorCodeAdapter.notifyDataSetChanged()
                binding.addProductColorCodeEdit.setText("")

            }
        } else if (v == binding.addProductCaptureImage) {
            onSelectImage()
        } else if (v == binding.addProductImgAddBtn) {
            if (binding.addProductCaptureImage.drawable != null) {
                listImage.add(ImageCaptureListModel(imagePath))
                imageCaptureListAdapter.notifyDataSetChanged()
                binding.addProductCaptureImage.setImageResource(0)
            }
        } else if (v == binding.addProductSubmit) {
            var jsonObject = JSONObject()
            //for allEditText
            jsonObject.put("productName", binding.productname.text.toString().trim())
            jsonObject.put("skuNo", binding.skuNoEdt.text.toString().trim())
            //for all Spinner

            jsonObject.put("subCatogary", binding.spinSubCatogary.selectedItem.toString())
            jsonObject.put("productFor", binding.spinProductFor.selectedItem.toString())
            jsonObject.put("brand", binding.spinBrandType.selectedItem.toString())
            jsonObject.put("productSize", binding.SpinProductSize.selectedItem.toString())
            jsonObject.put("price", binding.productPrice.text.toString().trim())
            jsonObject.put("priceLive", binding.livePrice.text.toString().trim())
            jsonObject.put("description", binding.description.text.toString().trim())


            //for  Color
            if (clrList != null && clrList.size > 0) {
                var jsonArrayClr = JSONArray()
                for (i in 0 until clrList.size) {
                    jsonArrayClr.put(clrList[i].colorCode)
                }
                jsonObject.put(colorKeySave, jsonArrayClr)
            }
            Log.d("ok", "jsonObject" + jsonObject)
            addProductAPICall(jsonObject)
        }
    }


    private fun setToolBar() {
        Utils.changeStatusTextColor2(this)
        binding.toolbarAddProduct.toolbarTitle.text = "Add Product"
        binding.toolbarAddProduct.ivBack.setOnClickListener { finish() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        setTouchNClick(binding.addProductClrPicker)
        setTouchNClick(binding.addProductClrAdd)
        setTouchNClick(binding.addProductCaptureImage)
        setTouchNClick(binding.addProductImgAddBtn)
        setTouchNClick(binding.addProductSubmit)
        fontRobotoRegular = resources.getFont(R.font.roboto_regular)
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

    private fun setImageList() {
        imageCaptureListAdapter = ImageCaptureListAdapter(
            this@AddProductActvity,
            listImage,
            object : ImageCaptureListAdapter.ClickListener {
                override fun onClick(pos: Int) {
                    var listSize = listImage.size
                    listImage.removeAt(pos)
                    imageCaptureListAdapter.notifyItemRemoved(pos);
                    imageCaptureListAdapter.notifyItemRangeChanged(pos, listSize)
                    imageCaptureListAdapter.notifyItemChanged(pos)
                }

            })

        binding.addProductRecycleImgList.also {
            it.layoutManager =
                LinearLayoutManager(this@AddProductActvity, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = imageCaptureListAdapter
        }
    }

    private fun setListColor() {
        colorCodeAdapter = ColorCodeAdapter(
            this@AddProductActvity,
            clrList,
            object : ColorCodeAdapter.ClickListener {
                override fun onClick(pos: Int) {
                    var listSize = clrList.size
                    clrList.removeAt(pos)
                    colorCodeAdapter.notifyItemRemoved(pos);
                    colorCodeAdapter.notifyItemRangeChanged(pos, listSize)
                    colorCodeAdapter.notifyItemChanged(pos)
                }

            })

        binding.addProductRecyclerColor.also {
            it.layoutManager =
                LinearLayoutManager(this@AddProductActvity, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = colorCodeAdapter
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
                    //storePath
                    imagePath = selectedMedia[0].path!!


                    val bitmap: Bitmap?
                    bitmap = BitmapFactory.decodeFile(selectedMedia[0].path)
                    //imageUrl = Uri.parse(selectedMedia[0].path)

                    try {
                        binding.addProductCaptureImage.setImageBitmap(null)
                        binding.addProductCaptureImage.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        Log.d("crashImage", "onActivityResult: " + e)
                    }

                    //  setBody(bitmap!!, "vehicleDocument")

                }
            }
        }

    private fun addProductAPICall(jsonObject: JSONObject) {
        progressDialog.show(THIS!!)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        val addProductViewModel = CommonViewModel(THIS!!)

        builder.addFormDataPart("shopCategoryId", storeData.shopCategoryId)
        builder.addFormDataPart("shopSubCategoryId", storeData.shopSubCategoryId)
        builder.addFormDataPart("vendorShopId", storeData._id)
//        json.put("productData", jsonObject)

        builder.addFormDataPart("productData",jsonObject.toString())
        var listMultipartBody: java.util.ArrayList<MultipartBody.Part> = java.util.ArrayList()

        for (i in 0 until listImage.size){
            var reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), File(listImage[i].img))
            var body = MultipartBody.Part.createFormData("images", File(listImage[i].img).name, reqFile)
            listMultipartBody.add(body)
        }

        for (i in 0 until listMultipartBody.size){
            builder.addPart(listMultipartBody[i])
        }

        addProductViewModel.addproduct(builder.build()).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "addProductAPICall: ")
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        Toast.makeText(this, ""+it.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }


    private fun getAttributeApi() {
        progressDialog.show(THIS!!)
        var addProductViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()

        json.put("shopId", storeData._id)

        addProductViewModel.attrubuteList(json).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "addProductAPICall: ")
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        attributeList.addAll(it.data)
                        var subCat =kotlin.collections.ArrayList<String>()
                        subCat.add("Select sub category")
                        for (i in 0 until attributeList.size){
                            subCat.add(attributeList[0].subCategoryName)

                        }
                        val adapter = ArrayAdapter(
                            this,

                            android.R.layout.simple_spinner_item, subCat,
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinSubCatogary.adapter = adapter
                        binding.spinSubCatogary.onItemSelectedListener = this@AddProductActvity
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val brand = kotlin.collections.ArrayList<String>()
        val size = kotlin.collections.ArrayList<String>()
        val productFor = kotlin.collections.ArrayList<String>()
         when(parent?.id){
             R.id.spinSubCatogary->{
                 if(position>0) {
                     brand.clear()
                     brand.add("Select brand")
                     for (i in 0 until attributeList[position-1].attributes[0].data.size) {
                         brand.add(attributeList[position-1].attributes[0].data[i])

                     }
                     adapterBrand = ArrayAdapter(
                         this,

                         android.R.layout.simple_spinner_item, brand,
                     )
                     adapterBrand?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                     binding.spinBrandType.adapter = adapterBrand
                     binding.spinBrandType.onItemSelectedListener = this@AddProductActvity

                     size.clear()
                     size.add("Select size")
                     for (i in 0 until attributeList[position-1].attributes[1].data.size) {
                         size.add(attributeList[position-1].attributes[1].data[i])

                     }
                     adapterSize = ArrayAdapter(
                         this,

                         android.R.layout.simple_spinner_item, size,
                     )
                     adapterSize?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                     binding.SpinProductSize.adapter = adapterSize
                     binding.SpinProductSize.onItemSelectedListener = this@AddProductActvity

                     productFor.clear()
                     productFor.add("Select product for")
                     for (i in 0 until attributeList[position-1].attributes[2].data.size) {
                         productFor.add(attributeList[position-1].attributes[2].data[i])

                     }
                       adapterproductfor = ArrayAdapter(
                         this,

                         android.R.layout.simple_spinner_item, productFor,
                     )
                     adapterproductfor?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                     binding.spinProductFor.adapter = adapterproductfor
                     binding.spinProductFor.onItemSelectedListener = this@AddProductActvity


                 }else{
                    brand.clear()
                     size.clear()
                     productFor.clear()
                     adapterBrand?.clear()
                     adapterSize?.clear()
                     adapterproductfor?.clear()

                 }
             }else->{

         }
         }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}