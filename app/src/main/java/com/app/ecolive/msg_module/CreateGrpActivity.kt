package com.app.ecolive.msg_module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.CreateGrpActivityBinding
import com.app.ecolive.localmodel.UserListModel
import com.app.ecolive.msg_module.adapter.CreateGrpListAdapter
import com.app.ecolive.utils.Utils
import com.cometchat.pro.models.User

class CreateGrpActivity : AppCompatActivity() {

    lateinit var binding: CreateGrpActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@CreateGrpActivity,R.layout.create_grp_activity)
        setToolBar()

        cometchat.userlist(object :CometChatInterface{
            override fun getAllUserList(list: List<User?>) {
                setData(list)
             }

        })
    }

    lateinit var chatListAdapter: CreateGrpListAdapter
    private fun setData(listUser: List<User?>) {

        val list =ArrayList<UserListModel>()

        for (i in listUser.indices){
            list.add(UserListModel(listUser[i]!!.name,listUser[i]!!.uid,listUser[i]!!.avatar?:""))

        }
        chatListAdapter = CreateGrpListAdapter(this@CreateGrpActivity,list,object:CreateGrpListAdapter.ClickListener{
            override fun onClick(id: String) {
                startActivity(Intent(this@CreateGrpActivity,ChatActivity::class.java).putExtra("id",id))

            }

        })

        binding.recycleCreateGrp.also {
            it.layoutManager = LinearLayoutManager(this@CreateGrpActivity,LinearLayoutManager.VERTICAL,false)
            it.adapter = chatListAdapter
        }

    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)

        binding.toolbarChatList.toolbarTitle.text="Messaging"
        binding.toolbarChatList.ivBack.setOnClickListener { finish() }
    }
}