package com.app.ecolive.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R


import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.offercity.base.BaseActivity


/**
 * Created by Dinesh Choudhary on 24-oct-17.
 */
class DialogCustmYesNo : BaseActivity() {
    private var mContext: Context? = null
    private var customDialog: Dialog? = null
    lateinit var dialogclick: Dialogclick
    lateinit var action_yes: TextView

    lateinit var action_reset: TextView
    internal lateinit var title: String
    internal lateinit var errrMsg: String

    // internal lateinit var getSelectedText: String
    private lateinit var linearLayoutManager: LinearLayoutManager
    // lateinit var nameList: List<RadioItemPojo>

    fun createDialog(mContext: Context, title: String,errrMsg: String,btnOk:String,btnNo:String, dialogclick: Dialogclick) {
        this.mContext = mContext
        this.dialogclick = dialogclick
        this.title = title
        this.errrMsg = errrMsg

//        customDialog = Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert)
//        customDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
//        customDialog!!.setContentView(R.layout.okcancel_item)
//        customDialog!!.setCanceledOnTouchOutside(false)
//        customDialog!!.show()
       // init()
        val builder = MaterialAlertDialogBuilder(mContext, R.style.Theme_MyApp_Dialog_Alert)
        builder.setCancelable(false)
        builder.setTitle(title).setMessage(errrMsg)
            .setPositiveButton(btnOk) { dialog, which ->
                dialog.dismiss()
                dialogclick.onYES()
            }
            .setNegativeButton(btnNo){dialog,which->
                dialog.dismiss()
                dialogclick.onNO()
            }

        val alert = builder.create()
        alert.show()

    }
    fun createDialogOK(mContext: Context, title: String,errrMsg: String, dialogclick: Dialogclick) {
        this.mContext = mContext
        this.dialogclick = dialogclick
        this.title = title
        this.errrMsg = errrMsg

        val builder = MaterialAlertDialogBuilder(mContext, R.style.Theme_MyApp_Dialog_Alert)
        builder.setTitle(title).setMessage(errrMsg)
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
                dialogclick.onYES()
            }


        val alert = builder.create()
        alert.show()

    }



    interface Dialogclick {

        fun onYES()
        fun onNO()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v == action_yes) {
            customDialog!!.dismiss()
            dialogclick.onYES()
        }
        if (v == action_reset) {
            customDialog!!.dismiss()


        }
    }

    companion object {
        private var instance = null
        fun getInstance(): DialogCustmYesNo = DialogCustmYesNo().apply {
            return@apply
        }
    }
}
