package com.nightout.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


import android.widget.Button
import android.widget.LinearLayout
import com.app.ecolive.R
import com.app.ecolive.shop_owner.AddResturantItemActivity
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.AppConstant


class SelectSourceBottomSheetFragment(private val onSelectOptionListener: OnSelectOptionListener, s: String) :
    BottomSheetDialogFragment() {
    private lateinit var  cameraImageView:LinearLayout
    private lateinit var galleryImageView:LinearLayout
    private lateinit var closeDialog: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.image_picker_bottom_sheet, container, false)
        initView(v)
        init()
        return v
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun initView(view: View) {
        cameraImageView = view.findViewById(R.id.cameraImage)
        galleryImageView = view.findViewById(R.id.galleryImage)
        closeDialog = view.findViewById(R.id.closeDialog)

    }

    private fun init() {
        cameraImageView.setOnClickListener {
            onSelectOptionListener.onOptionSelect(AppConstant.CAMERA_KEY)
        }

        galleryImageView.setOnClickListener {
            onSelectOptionListener.onOptionSelect(AppConstant.GALLERY_KEY)
        }

        closeDialog.setOnClickListener {
          dismiss()
        }
    }


}