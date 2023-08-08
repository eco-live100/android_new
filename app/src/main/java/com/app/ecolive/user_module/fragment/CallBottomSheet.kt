package com.nightout.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.CallBottomSheetBinding

import com.app.ecolive.user_module.interfacee.OnSelectOptionListener


class CallBottomSheet(private val onSelectOptionListener: OnSelectOptionListener, s: String) : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: CallBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.call_bottom_sheet, container, false)
        initView()
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun initView() {
      //  binding.locCloseBtn.setOnClickListener(this)



    }



    override fun onClick(v: View?) {
//
//          if(v==binding.locCloseBtn){
//            dismiss()
//        }

//        else if(v==binding.sssss){
//              onSelectOptionListener.onOptionSelect(resources.getString(R.string.bid_new))
//              dismiss()
//          }
    }


}