package com.app.ecolive.user_module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.MyaddressActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.user_module.user_adapter.MyAddressAdapter
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import org.json.JSONObject

class MyAddressActivity : AppCompatActivity() {
    lateinit var binding:MyaddressActivityBinding
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@MyAddressActivity,R.layout.myaddress_activity)
        setToolBar()
        setDuumyRecycle()
    }

    lateinit var myAddressAdapter: MyAddressAdapter
    private fun setDuumyRecycle() {


        binding.addressAddBtn.setOnClickListener {
            startActivity(Intent(this@MyAddressActivity,AddAddressActivity::class.java))
        }

    }

    private fun setToolBar() {
        binding.toolbarAddress.toolbarTitle.text="Saved Address"
        binding.toolbarAddress.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        myAddressAdapter = MyAddressAdapter(this@MyAddressActivity,object:MyAddressAdapter.ClickListener{
            override fun onClick(pos: String) {
                deleteAddress(pos)
            }

        })
        binding.addressRecycle.also {
            it.adapter = myAddressAdapter
            it.layoutManager = LinearLayoutManager(this@MyAddressActivity,LinearLayoutManager.VERTICAL,false)
        }
    }

    override fun onStart() {
        super.onStart()
        getAddress()
    }

    private fun getAddress() {
        progressDialog.show(this)
        var commanViewModel = CommonViewModel(this)
        var json = JSONObject()


        commanViewModel.getAddress(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {

                        myAddressAdapter.addData(it.data)

                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun deleteAddress(id: String) {
        progressDialog.show(this)
        var commanViewModel = CommonViewModel(this)
        var json = JSONObject()

        json.put("addressId", id)
        commanViewModel.deleteAddress(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        getAddress()
                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
}