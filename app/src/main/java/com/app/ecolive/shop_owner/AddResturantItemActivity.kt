package com.app.ecolive.shop_owner

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAddResturantItemBinding
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

class AddResturantItemActivity : AppCompatActivity() ,OnSelectOptionListener{
    private lateinit var selectSourceBottomSheetFragment: SelectSourceBottomSheetFragment
    var imagePath = ""
    lateinit var binding:ActivityAddResturantItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_resturant_item)
        if(intent.extras!=null){
            if (intent.getStringExtra("from").toString()=="food"){
                binding.toolbar.toolbarTitle.text ="Add food item"

            }else{
                binding.toolbar.toolbarTitle.text ="Add grocery item"
                binding.llbtn.visibility =View.VISIBLE
                binding.llcheckbox.visibility =View.VISIBLE
                binding.addItem.visibility =View.GONE
            }
        }
          Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor2(this)
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

}