package com.app.ecolive.pharmacy_module

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyInsuranceBinding
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.nightout.ui.fragment.SelectSourceBottomSheetFragment

class PharmacyInsuranceActivity : AppCompatActivity(), OnSelectOptionListener {
    lateinit var binding :ActivityPharmacyInsuranceBinding
    var name =""
    var address =""
    var ssn =""
    var imagePath = ""
    private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_insurance)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Eco- Live Health Profile"
        binding.toolbar.help.visibility = View.VISIBLE
        if(intent.extras!=null){
            name =intent.getStringExtra("name")!!
            address =intent.getStringExtra("address")!!
            ssn =intent.getStringExtra("ssn")!!
        }
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (binding.isinsuranceSelected.isSelected){
                startActivity(Intent(this,MedicationListActivity::class.java)
                    .putExtra("name",name)
                    .putExtra("address",address)
                    .putExtra("ssn",ssn)
                    .putExtra("imagePath",imagePath)
                )
            }else{
                startActivity(Intent(this,MedicationListActivity::class.java)
                    .putExtra("name",name)
                    .putExtra("address",address)
                    .putExtra("ssn",ssn)
                    .putExtra("imagePath",imagePath)

                )
            }

        }
        binding.imageView6.setOnClickListener {
            onSelectImage()
        }


    }

    private fun onSelectImage() {
        if (!Utils.checkingPermissionIsEnabledOrNot(this)) {
            Utils.requestMultiplePermission(this, VehicleInfoActivity.requestPermissionCode)
        } else {
            selectSourceBottomSheetFragment = SelectSourceBottomSheetFragment(this, "")
            selectSourceBottomSheetFragment.show(
                this.supportFragmentManager,
                "selectSourceBottomSheetFragment"
            )
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
                    if(imagePath !=""){
                        binding.btnContinue.isEnabled =true
                        binding.btnContinue.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.color_blue))
                    }

                    val bitmap: Bitmap?
                    bitmap = BitmapFactory.decodeFile(selectedMedia[0].path)
                    //imageUrl = Uri.parse(selectedMedia[0].path)

                    try {
                        binding.insuranceImg.setImageBitmap(null)
                        binding.insuranceImg.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        Log.d("crashImage", "onActivityResult: " + e)
                    }

                    //  setBody(bitmap!!, "vehicleDocument")

                }
            }
        }
}