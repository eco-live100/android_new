package com.app.ecolive.shop_owner


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
import com.mrudultora.colorpicker.ColorPickerPopUp
import com.mrudultora.colorpicker.ColorPickerPopUp.OnPickColorListener
import com.app.ecolive.rider_module.fragment.SelectSourceBottomSheetFragment
import com.app.ecolive.utils.getFilePath
import com.app.ecolive.utils.toast
import com.offercity.base.BaseActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.collections.ArrayList


class AddProductActvity : BaseActivity() {
    lateinit var binding: AddproductActivityBinding

    var clrList = ArrayList<ColorModel>()
    private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
    lateinit var imageCaptureListAdapter: ImageCaptureListAdapter
    var listImage = ArrayList<ImageCaptureListModel>()
     private val progressDialog = CustomProgressDialog()
    private val REQUEST_CAMERA_PERMISSION = 1

    var imagePath: Uri? = null

    lateinit var storeData: ShopListModel.Data

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@AddProductActvity, R.layout.addproduct_activity)
        initView()
        setToolBar()

        setImageList()
        //  runTimeSpinner()
        getDataIntent()
        // productAttributeFormAPICall()


    }

    private fun getDataIntent() {
        storeData = intent.getSerializableExtra(AppConstant.STORE_DATA) as ShopListModel.Data
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v == binding.addProductCaptureImage) {
            imagePopup()
        } else if (v == binding.addProductImgAddBtn) {
            if (binding.addProductCaptureImage.drawable != null) {
                listImage.add(ImageCaptureListModel(imagePath!!))
                imageCaptureListAdapter.notifyDataSetChanged()
                binding.addProductCaptureImage.setImageResource(0)
            }
        } else if (v == binding.addProductSubmit) {

            if (listImage.size<1){
                toast("Add images")
                return
            }
            if (binding.productname.text.toString().trim() ==""){
                toast("Enter product name")
                return
            }
            if (binding.freeDeliveryEDt.text.toString().trim() ==""){
                toast("Enter Free delivery time")
                return
            }
            if (binding.fastDeliveryEDt.text.toString().trim() ==""){
                toast("Enter Fast delivery time")
                return
            }
            if (binding.productPrice.text.toString().trim() ==""){
                toast("Enter product price")
                return
            }

            if (binding.description.text.toString().trim() ==""){
                toast("Enter description")
                return
            }
            addProductAPICall()
        }
    }


    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        binding.toolbarAddProduct.toolbarTitle.text = "Add Product"
        binding.toolbarAddProduct.ivBack.setOnClickListener { finish() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {

        setTouchNClick(binding.addProductCaptureImage)
        setTouchNClick(binding.addProductImgAddBtn)
        setTouchNClick(binding.addProductSubmit)

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


    private fun addProductAPICall() {
        progressDialog.show(THIS!!)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        val addProductViewModel = CommonViewModel(THIS!!)

        builder.addFormDataPart("shopCategoryId", storeData.shopCategoryId?:"")
        builder.addFormDataPart("shopSubCategoryId",  storeData.shopSubCategoryId?:"")
        builder.addFormDataPart("vendorShopId", storeData._id?:"")
        builder.addFormDataPart("name", binding.productname.text.toString().trim())
        builder.addFormDataPart("freeDelivery", binding.freeDeliveryEDt.text.toString().trim())
        builder.addFormDataPart("fastDeliver", binding.fastDeliveryEDt.text.toString().trim())
        builder.addFormDataPart("price", binding.productPrice.text.toString().trim())
        builder.addFormDataPart("livePrice", binding.livePrice.text.toString().trim())
        builder.addFormDataPart("productData", binding.description.text.toString().trim())

        var listMultipartBody: java.util.ArrayList<MultipartBody.Part> = java.util.ArrayList()

       /* for (i in 0 until listImage.size) {
            val filePath = getFilePath(this, listImage[i].img)
            var reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), filePath!!)
            var body = MultipartBody.Part.createFormData("images", File(filePath).name, reqFile)
            listMultipartBody.add(body)
        }*/

        for (i in 0 until listImage.size) {
            builder.addPart(Utils.multipartBodyFile(this, listImage[i].img!!, "images"))
        }

        addProductViewModel.addproduct(builder.build()).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "addProductAPICall: ")
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
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


            if (data.data != null) {
                //storePath
                imagePath = data.data!!


                try {
                    binding.addProductCaptureImage.setImageURI(null)
                    binding.addProductCaptureImage.setImageURI(imagePath)

                } catch (e: Exception) {
                    Log.d("crashImage", "onActivityResult: " + e)
                }

                //  setBody(bitmap!!, "vehicleDocument")

            }

        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            var imageUri = getImageUri(this, imageBitmap!!)

            if (imageUri != null) {
                //storePath
                imagePath = imageUri


                try {
                    binding.addProductCaptureImage.setImageURI(null)
                    binding.addProductCaptureImage.setImageURI(imagePath)

                } catch (e: Exception) {
                    Log.d("crashImage", "onActivityResult: " + e)
                }

                //  setBody(bitmap!!, "vehicleDocument")

            }

        }
    }


}