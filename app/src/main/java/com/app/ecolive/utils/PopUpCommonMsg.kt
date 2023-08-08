package com.app.ecolive.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.app.ecolive.R
import com.offercity.base.BaseActivity

/**
 * Created by Rakesh on 24-oct-17.
 */
class PopUpCommonMsg : BaseActivity() {
    private var mContext: Context? = null
    private var customDialog: Dialog? = null
    lateinit var dialogclick: Dialogclick
    lateinit var action_ok: TextView
    lateinit var action_cancel: TextView
    lateinit var popupClose: ImageView
    internal lateinit var popupMsg: TextView
    internal lateinit var title: String


    fun createDialog(mContext: Context, title: String, dialogclick: Dialogclick) {
        this.mContext = mContext
        this.dialogclick = dialogclick
        this.title = title

        customDialog = Dialog(mContext,android.R.style.ThemeOverlay_Material_Dialog_Alert)
        customDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        customDialog!!.setContentView(R.layout.popup_common)
        customDialog!!.setCanceledOnTouchOutside(false)
        customDialog!!.show()

        init()
    }

    private fun init() {
        popupMsg = customDialog!!.findViewById(R.id.popupMsg)
        popupMsg.text = title
        action_cancel= customDialog!!.findViewById(R.id.popupCancel)
        popupClose= customDialog!!.findViewById(R.id.popupClose)
        setTouchNClick(action_cancel)
        setTouchNClick(popupClose)
        action_ok = customDialog!!.findViewById(R.id.popupOk)
        setTouchNClick(action_ok)


    }

    interface Dialogclick {
        fun onYes()
        fun onNo()

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v === action_ok) {
            customDialog!!.dismiss()
            dialogclick.onYes()
        }
        if (v === action_cancel) {
            customDialog!!.dismiss()
            dialogclick.onNo()

        }
        if (v === popupClose) {
            customDialog!!.dismiss()

        }
    }


    companion object {
        private var instance=null
        fun getInstance(): PopUpCommonMsg= PopUpCommonMsg().apply {
            return@apply
        }
    }
}
