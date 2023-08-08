package com.app.ecolive.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
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
class PopUpVehicleChoose : BaseActivity() {
    private var mContext: Context? = null
    private var customDialog: Dialog? = null
    lateinit var dialogclick: Dialogclick


    lateinit var popUpVehicleClose: ImageView
    lateinit var popUpConfirm: TextView
    lateinit var popPedestrian: TextView
    lateinit var popCycle: TextView
    lateinit var popElectric: TextView
    lateinit var popPertol: TextView
    lateinit var popBio: TextView

    internal lateinit var title: String


    fun createDialog(mContext: Context, title: String, dialogclick: Dialogclick) {
        this.mContext = mContext
        this.dialogclick = dialogclick
        this.title = title

        customDialog = Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert)
        customDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        customDialog!!.setContentView(R.layout.popup_vehiclechoose)
        customDialog!!.setCanceledOnTouchOutside(false)
        customDialog!!.show()

        init()
    }

    private fun init() {
        popUpVehicleClose = customDialog!!.findViewById(R.id.popUpVehicleClose)
        popUpConfirm = customDialog!!.findViewById(R.id.popUpConfirm)
        popPedestrian = customDialog!!.findViewById(R.id.popPedestrian)
        popCycle = customDialog!!.findViewById(R.id.popCycle)
        popElectric = customDialog!!.findViewById(R.id.popElectric)
        popPertol = customDialog!!.findViewById(R.id.popPertol)
        popBio = customDialog!!.findViewById(R.id.popBio)
        setTouchNClick(popUpVehicleClose)
        setTouchNClick(popUpConfirm)
        setTouchNClick(popPedestrian)
        setTouchNClick(popCycle)
        setTouchNClick(popElectric)
        setTouchNClick(popPertol)
        setTouchNClick(popBio)
        setNormalText()


        popUpConfirm.isEnabled=false
        popUpConfirm.isClickable=false
    }

    private fun setNormalText() {
        popPedestrian.setTypeface(null, Typeface.NORMAL);
        popCycle.setTypeface(null, Typeface.NORMAL);
        popElectric.setTypeface(null, Typeface.NORMAL);
        popPertol.setTypeface(null, Typeface.NORMAL);
        popBio.setTypeface(null, Typeface.NORMAL);
    }

    interface Dialogclick {
        fun onYes()
        fun onNo()
        fun onPedesstrain()
        fun onCycle()
        fun onElectric()
        fun onPetrol()
        fun onBio()

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v === popUpConfirm) {
            customDialog!!.dismiss()
            dialogclick.onYes()
        } else if (v === popUpVehicleClose) {
            customDialog!!.dismiss()

        } else if (v === popPedestrian) {
            dialogclick.onPedesstrain()
            setNormalText()
            popPedestrian.setTypeface(null, Typeface.BOLD);
           confrimBtnEnable()
        } else if (v === popCycle) {
            dialogclick.onCycle()
            setNormalText()
            popCycle.setTypeface(null, Typeface.BOLD);
            confrimBtnEnable()
        } else if (v === popElectric) {
            dialogclick.onElectric()
            setNormalText()
            popElectric.setTypeface(null, Typeface.BOLD);
            confrimBtnEnable()
        } else if (v === popPertol) {
            dialogclick.onPetrol()
            setNormalText()
            popPertol.setTypeface(null, Typeface.BOLD)
            confrimBtnEnable()
        } else if (v === popBio) {
            dialogclick.onBio()
            setNormalText()
            popBio.setTypeface(null, Typeface.BOLD)
            confrimBtnEnable()
        }
    }

    private fun confrimBtnEnable() {
        popUpConfirm.isEnabled=true
        popUpConfirm.isClickable=true
        popUpConfirm.setTextColor(mContext?.resources!!.getColor(R.color.black))
    }


    companion object {
        private var instance = null
        fun getInstance(): PopUpVehicleChoose = PopUpVehicleChoose().apply {
            return@apply
        }
    }
}
