package com.app.ecolive.cometchat.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.FragmentChatBinding
import com.app.ecolive.databinding.FragmentUserBinding
import com.app.ecolive.msg_module.ChatActivity
import com.app.ecolive.msg_module.CometChatInterface
import com.app.ecolive.msg_module.adapter.ChatListAdapter
import com.app.ecolive.msg_module.cometchat
import com.cometchat.pro.models.Conversation


class ChatFragment : Fragment() {

    lateinit var bin : FragmentChatBinding
    lateinit var chatListAdapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bin=FragmentChatBinding.inflate(layoutInflater)
        lisner()
        return bin.root
    }

    private fun lisner() {
        val listRecentChat =ArrayList<Conversation>()
        cometchat.getRecentChat(object : CometChatInterface {
            override fun onRecentChat(list: List<Conversation>?) {
                for (i in list!!.indices){

                    listRecentChat.add(list[i])
                    chatListAdapter.notifyDataSetChanged()


                }
            }


        })





        chatListAdapter = ChatListAdapter(requireContext(),listRecentChat,object:
            ChatListAdapter.ClickListener{
            override fun onClick(id: String) {
                startActivity(Intent(requireContext(), ChatActivity::class.java).putExtra("id",id))
            }

            override fun onClickImg(pos: Int) {
                // startActivity(Intent(this@ChatListActivity,FullImageActivity::class.java))
            }

        })

        bin.recycleChatList.also {
            it.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL,false)
            it.adapter = chatListAdapter
        }

    }


}