package com.nightout.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


import android.widget.Button
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.BottmsheetDelBinding
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.AppConstant


class BottomSheetDel(private val onSelectOptionListener: OnSelectOptionListener, private val dataInfo: ShopListModel.Data) :
    BottomSheetDialogFragment() {

    private lateinit var closeDialog: Button
    lateinit var binding : BottmsheetDelBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // val v = inflater.inflate(R.layout.bottmsheet_del, container, false)
        binding = DataBindingUtil.inflate(inflater,R.layout.bottmsheet_del,container,false)
        var view = binding.root
        initView(view)
        init()
        return view
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun initView(view: View) {
//        cameraImageView = view.findViewById(R.id.cameraImage)
//        galleryImageView = view.findViewById(R.id.galleryImage)
//        closeDialog = view.findViewById(R.id.closeDialog)

    }

    private fun init() {
        binding.botmSheetTitle.text = dataInfo.shopName

        binding.botmSheetDel.setOnClickListener {
            onSelectOptionListener.onOptionSelect(AppConstant.DELETE_KEY)
        }

//        closeDialog.setOnClickListener {
//          dismiss()
//        }
    }


}