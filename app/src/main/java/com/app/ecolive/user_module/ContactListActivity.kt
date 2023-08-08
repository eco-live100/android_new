package com.app.ecolive.user_module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ContactlistActivityBinding
import com.app.ecolive.msg_module.DialActivity
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.user_module.model.UserModel
import com.app.ecolive.user_module.user_adapter.ContactListAdapter
import com.app.ecolive.utils.Utils
import com.nightout.ui.fragment.CallBottomSheet

class ContactListActivity : AppCompatActivity(), OnSelectOptionListener {
    lateinit var binding: ContactlistActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ContactListActivity,R.layout.contactlist_activity)
       setToolBar()
        setDummyLIst()
    }

    lateinit var contactListAdapter: ContactListAdapter
    private fun setDummyLIst() {
        var list= ArrayList<UserModel>()
        list.add(UserModel("Gail Forcewind","Today at 11:47 AM",R.drawable.dummy_female_user,R.drawable.ic_missed_call))
        list.add(UserModel("Esther Howard","Today at 11:57 AM",R.drawable.dummy_male_user,R.drawable.ic_incoming_call2))
        list.add(UserModel("Cameron Williamson","Friday at 05:30 PM",R.drawable.dummy_male_user,R.drawable.ic_missed_call))
        list.add(UserModel("Gail Forcewind","Today at 11:47 AM",R.drawable.dummy_female_user,R.drawable.ic_missed_call))
        list.add(UserModel("Esther Howard","Today at 11:57 AM",R.drawable.dummy_male_user,R.drawable.ic_incoming_call2))
        list.add(UserModel("Cameron Williamson","Friday at 05:30 PM",R.drawable.dummy_male_user,R.drawable.ic_missed_call))
        list.add(UserModel("Gail Forcewind","Today at 11:47 AM",R.drawable.dummy_female_user,R.drawable.ic_missed_call))
        list.add(UserModel("Esther Howard","Today at 11:57 AM",R.drawable.dummy_male_user,R.drawable.ic_incoming_call2))
        list.add(UserModel("Cameron Williamson","Friday at 05:30 PM",R.drawable.dummy_male_user,R.drawable.ic_missed_call))
        contactListAdapter = ContactListAdapter(this@ContactListActivity,list,object :ContactListAdapter.ClickListener{
            override fun onClick(pos: Int) {
                showBotomSheet()
            }

        })
        binding.contactRecycle.also {
            it.adapter = contactListAdapter
            it.layoutManager = LinearLayoutManager(this@ContactListActivity,LinearLayoutManager.VERTICAL,false)
        }
        binding.contactRecycle.adapter= contactListAdapter

    }
    lateinit var callBottomSheet: CallBottomSheet

    private fun showBotomSheet() {
        callBottomSheet = CallBottomSheet(this,"")
        callBottomSheet.show(this@ContactListActivity.supportFragmentManager,"CALLBOTTOM")
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbarContactList.toolbarTitle.text= "Contacts"
        binding.toolbarContactList.ivBack.setOnClickListener { finish() }
        binding.dialFab.setOnClickListener {
            startActivity(Intent(this,DialActivity::class.java))
        }
    }

    override fun onOptionSelect(option: String) {
         when(option){
              "sdsds"->{
                 //do logoc
             }
         }
    }
}