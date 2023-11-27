package com.app.ecolive.pharmacy_module

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPrescriptionRequestBinding
import com.app.ecolive.utils.Utils
import java.io.ByteArrayOutputStream

class PrescriptionRequestActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrescriptionRequestBinding
    var file = 1
    private var picture = 2
    var option = 1
    private val REQUEST_CAMERA_PERMISSION = 1
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_prescription_request)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Prescription request form to"
        binding.toolbar.help.visibility = View.VISIBLE
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

//        binding.submitButton.setOnClickListener {
//            startActivity(Intent(this,PrescriptionRequestSendByActivity::class.java))
//        }

        intent.extras?.let {
            val doctorName = it.getString("doctorName").toString()
            val doctorProfile = it.getString("doctorProfile").toString()
            binding.doctorName.text = doctorName

        }

        binding.fileUploadLL.setOnClickListener {
            imagePopup()
            option = file
        }
        binding.pictureUploadLL.setOnClickListener {
            imagePopup()
            option = picture
        }
        binding.submitButton.setOnClickListener {

            if (binding.symptomsEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Enter symptoms", Toast.LENGTH_SHORT).show()
            } else if (binding.symptomsDurationEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Enter symptoms duration", Toast.LENGTH_SHORT).show()
            } else if (binding.alreadyTakingMedEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Enter taking medications", Toast.LENGTH_SHORT).show()
            } else if (binding.recentHistoryEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Please enter recent history", Toast.LENGTH_SHORT).show()
            }else if (binding.allergiesEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Please enter allergies", Toast.LENGTH_SHORT).show()
            }else if (binding.smokingEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Please enter smoking and any drug", Toast.LENGTH_SHORT).show()
            } else if (binding.otherInfoEt.text?.isEmpty() == true) {
                Toast.makeText(this, "Enter enter other info", Toast.LENGTH_SHORT).show()
            } else if (!binding.aboveStatementsCheckBox.isChecked) {
                Toast.makeText(this, "please confirm above statements", Toast.LENGTH_SHORT).show()
            }else if (!binding.youMaySendCheckBox.isChecked) {
                Toast.makeText(this, "please confirm my prescriptions", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this,PrescriptionRequestSendByActivity::class.java))
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
            if(option==file){
                //binding.logoImage.setImageURI(imageUri)
            }
            if(option==picture){
                //binding.backgroundImg.setImageURI(imageUri)
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            val extras: Bundle = data.extras!!
            val imageBitmap = extras["data"] as Bitmap?
            imageUri = getImageUri(this, imageBitmap!!)
            if(option==file){
                //binding.logoImage.setImageURI(imageUri)
            }
            if(option==picture){
                //binding.backgroundImg.setImageURI(imageUri)
            }
            //binding.backgroundImg.setImageBitmap(imageBitmap)
            Log.d("TAG", "iamgedsfas:: $imageUri")
        }

    }

}