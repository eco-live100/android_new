package com.app.ecolive.user_module

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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ProfileupdateActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.ShopOwnerHomePageNavigationActivity
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.getFilePath
import com.app.ecolive.viewmodel.CommonViewModel
import com.bumptech.glide.Glide
import com.zegocloud.zimkit.services.ZIMKit
import com.zegocloud.zimkit.services.callback.UserAvatarUrlUpdateCallback
import im.zego.zim.entity.ZIMError
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class ProfileUpdateActivity : AppCompatActivity() {
    lateinit var binding: ProfileupdateActivityBinding
    private val progressDialog = CustomProgressDialog()
    var profileImage: MultipartBody.Part? = null
    private lateinit var reqFile: RequestBody
    private val REQUEST_CAMERA_PERMISSION = 1

    private var filePath: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@ProfileUpdateActivity,
            R.layout.profileupdate_activity
        )

        setToolBar()

    }

    private fun setToolBar() {
        binding.toolbarProfileUpdate.toolbarTitle.text = "Profile Update"
        binding.toolbarProfileUpdate.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)

        binding.profileUpdateName.setText("${PreferenceKeeper.instance.loginResponse?.firstName} ${PreferenceKeeper.instance.loginResponse?.lastName}")
        binding.profileUpdateEmail.text = PreferenceKeeper.instance.loginResponse?.email
        "${PreferenceKeeper.instance.loginResponse?.countryCode}${PreferenceKeeper.instance.loginResponse?.mobileNumber}".also {
            binding.profileUpdatePhno.text = it
        }
        Glide.with(this).load(PreferenceKeeper.instance.loginResponse?.profilePicture)
            .placeholder(R.drawable.ic_user_default).into(binding.userProfileImage)
        binding.photoImg.setOnClickListener {
            imagePopup()
        }
        binding.appCompatButton.setOnClickListener {
            if (filePath != null) {
                updateProfile()
            }

        }
    }

    private fun updateProfile() {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (e: Exception) {
        }
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("fullName", binding.profileUpdateName.text.toString().trim())

        //storeLogo
        if (profileImage != null) {
            builder.addPart(profileImage!!)
        }

        val profileModel = CommonViewModel(this)
        profileModel.updateProfileApi(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let { data ->
                        PreferenceKeeper.instance.loginResponse?.profilePicture =
                            data.data.profilePicture ?: ""
                        ZIMKit.updateUserAvatarUrl(
                            data.data.profilePicture
                                ?: "https://cdn-icons-png.flaticon.com/256/149/149071.png"
                        ) { _, _ -> }
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

    private fun setBodyStorLogo(imageUri: Uri, flag: String) {
        val filePath = getFilePath(this, imageUri!!)
        this.filePath = File(filePath)

        reqFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), this.filePath!!)
        profileImage = MultipartBody.Part.createFormData(flag, this.filePath!!.name, reqFile)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {


            binding.userProfileImage.setImageURI(data.data)
            imageCreaterForApi(data.data)


        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            var imageUri = getImageUri(this, imageBitmap!!)

            binding.userProfileImage.setImageURI(imageUri)
            imageCreaterForApi(imageUri)


        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun imageCreaterForApi(imageUri: Uri?) {

        setBodyStorLogo(imageUri!!, "profilePicture")

    }

}