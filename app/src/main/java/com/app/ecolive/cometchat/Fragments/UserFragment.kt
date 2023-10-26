package com.app.ecolive.cometchat.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.FragmentUserBinding
import com.app.ecolive.localmodel.UserListModel
import com.app.ecolive.msg_module.ChatActivity
import com.app.ecolive.msg_module.CometChatInterface
import com.app.ecolive.msg_module.adapter.CreateGrpListAdapter
import com.app.ecolive.msg_module.cometchat
import com.cometchat.pro.models.User


class UserFragment : Fragment() {
    lateinit var bin :FragmentUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bin=FragmentUserBinding.inflate(layoutInflater)
        lisner()
        return bin.root
    }

    private fun lisner() {
        bin.isShimmerShow =true
        cometchat.userlist(object : CometChatInterface {
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
        chatListAdapter = CreateGrpListAdapter(requireContext(),list,object:
            CreateGrpListAdapter.ClickListener{
            override fun onClick(id: String) {
                startActivity(Intent(requireContext(), ChatActivity::class.java).putExtra("id",id))

            }

        })

        bin.UserRecycle.also {
            it.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL,false)
            it.adapter = chatListAdapter
            bin.isShimmerShow =false
        }

    }

}