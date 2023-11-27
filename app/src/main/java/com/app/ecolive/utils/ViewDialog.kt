package com.app.ecolive.utils


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.app.ecolive.R
import org.webrtc.ContextUtils.getApplicationContext


class ViewDialog {
    fun showDialog(activity: Activity?) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_request_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mDialogNo = dialog.findViewById<ImageView>(R.id.frmNo)
        mDialogNo.setOnClickListener {
            Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        /*val mDialogOk = dialog.findViewById<FrameLayout>(R.id.frmOk)
        mDialogOk.setOnClickListener {
            Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }*/
        dialog.show()
    }
}