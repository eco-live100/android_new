package com.app.ecolive.payment_module

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.SelectpaymentactionActvityBinding
import com.app.ecolive.msg_module.FriendsListActivity
import com.app.ecolive.msg_module.InvoiceRequestActivity
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils
import com.offercity.base.BaseActivity

class SelectPaymentAction : BaseActivity(){
    lateinit var binding : SelectpaymentactionActvityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(THIS!!,R.layout.selectpaymentaction_actvity)
        setToolBar()
        binding.payatlocation.setOnClickListener(this)
        binding.sendMoneytofriend.setOnClickListener(this)
        binding.addMoneytoWallet.setOnClickListener(this)
        binding.localInterTransaction.setOnClickListener(this)
        binding.paySendPaymentReq.setOnClickListener(this)
        binding.makePaymentBusiness.setOnClickListener(this)
    }

    private fun setToolBar() {
        Utils.changeStatusTextColor2(this)
         binding.tolbarSelectPaymentAction.toolbarTitle.text = "Select a Payment action"
        binding.tolbarSelectPaymentAction.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.payatlocation->{
                showDialog()
            }

            R.id.sendMoneytofriend->{
                startActivity(
                    Intent(
                        this,
                        SendMoneyHomePageActivity::class.java
                    )
                )
            }
            R.id.addMoneytoWallet->{
            startActivity(
                Intent(
                    this,
                    AddMoneyMainActivity::class.java
                )
            )
            }

            R.id.localInterTransaction->{
                startActivity(Intent(this,SendMoneyInternationLocl::class.java))
            }

            R.id.paySendPaymentReq->{
                startActivity(Intent(this,InvoiceRequestActivity::class.java))
            }
            R.id.makePaymentBusiness->{
                showDialogChooseAction()
            }
        }
    }
    private fun showDialog() {
        var countPeople ="0"
        val dialog = Dialog(this )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_payatloction)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button
        val btnConfirm = dialog.findViewById(R.id.btnConfirm) as Button
        val radioPayAll = dialog.findViewById(R.id.radioPayAll) as RadioButton
        val radioPaymultiple = dialog.findViewById(R.id.radioPaymultiple) as RadioButton
        val txtminus = dialog.findViewById(R.id.txtminus) as TextView
        val txtPlus = dialog.findViewById(R.id.txtPlus) as TextView
        val txtPeopleCount = dialog.findViewById(R.id.txtPeopleCount) as TextView

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            dialog.dismiss()
            if(radioPaymultiple.isChecked){
                startActivity(
                    Intent(this@SelectPaymentAction, FriendsListActivity::class.java)

                )
            }else{
                startActivity(
                    Intent(this@SelectPaymentAction, AddMoneyMainActivity::class.java)
                        .putExtra(AppConstant.INTENT_EXTRAS.IsFromHOME, true)
                )
            }


        }
        radioPayAll.setOnClickListener {
            radioPaymultiple.isChecked =false
            radioPayAll.isChecked =true
        }
        radioPaymultiple.setOnClickListener {
            radioPaymultiple.isChecked =true
            radioPayAll.isChecked =false
        }
        txtPlus.setOnClickListener {
             countPeople =(countPeople.toInt()+1).toString()
            txtPeopleCount.text = countPeople
        }
        txtminus.setOnClickListener {
            if (countPeople.toInt()>0){
                countPeople =(countPeople.toInt()-1).toString()
            }
            txtPeopleCount.text = countPeople

        }
        dialog.show()

    }

    private fun showDialogChooseAction() {
        val dialog = Dialog(this )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_layout_ngo)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        val paymentTobusiness = dialog.findViewById(R.id.paymentTobusiness) as RadioButton
        val paymenttoMultiplaePeople = dialog.findViewById(R.id.paymenttoMultiplaePeople) as RadioButton

        paymentTobusiness.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this,
                    SendMoneyHomePageActivity::class.java
                )
            )

        }
        paymenttoMultiplaePeople.setOnClickListener {
            dialog.dismiss()

        startActivity(Intent(this,RecipentListListActivity::class.java))

        }

        dialog.show()

    }

}