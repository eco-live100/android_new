package com.app.ecolive.msg_module

import android.util.Log
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.*
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*

object cometchat {

   // private val authKey = "7f39e01d184e45431cf38af316fd6f40c170ec84" // Replace with your App Auth Key
    private val authKey = "9179251c10cde462dc04a4aa0c9201af5381ceb0" // Replace with your App Auth Key

    var instance1:CometChatInterface ?=null
   fun register(userId: String,userName: String){

       val user = User()
       user.uid = userId // Replace with the UID for the user to be created
       user.name = userName// Replace with the name of the user

       CometChat.createUser(user, authKey, object : CometChat.CallbackListener<User>() {
           override fun onSuccess(user: User) {
               Log.d("createUser", user.toString())

           }

           override fun onError(e: CometChatException) {
               Log.e("createUser", e.message.toString())
           }
       })
   }

    fun login(userId: String) {
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(userId,authKey, object : CometChat.CallbackListener<User>() {
                override fun onSuccess(p0: User?) {
                    Log.d("TAG", "Login Successful : " + p0?.toString())
                }

                override fun onError(p0: CometChatException?) {
                    Log.d("TAG", "Login failed with exception: " +  p0?.message)
                }

            })
        }else{
            // User already logged in
        }
    }

    fun logout(){
        CometChat.logout(object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d("TAG", "Logout completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG", "Logout failed with exception: " + p0?.message)
            }

        })
    }

    fun userlist(instance: CometChatInterface) {
        var usersRequest: UsersRequest? = null
        val limit = 100

        usersRequest = UsersRequest.UsersRequestBuilder().setLimit(limit).build()

        usersRequest.fetchNext(object : CometChat.CallbackListener<List<User?>>() {
            override fun onSuccess(list: List<User?>) {
                Log.d("TAG", "User list received: " + list.size)
                instance.getAllUserList(list)

            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "User list fetching failed with exception: " + e.message)
            }
        })
    }

    fun singleUserDetail(userId: String,instance: CometChatInterface){
        val UID:String=userId

        CometChat.getUser(UID,object :CometChat.CallbackListener<User>(){
            override fun onSuccess(user: User?) {
                instance.getSingleUserDetails(user)
            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG", "User details fetching failed with exception: " + p0?.message)
            }
        })
    }

    fun getChatHistory(id:String,instance: CometChatInterface){
        val limit: Int = 30
        val UID: String = id

        val messagesRequest = MessagesRequest.MessagesRequestBuilder()
            .setLimit(limit)
            .setUID(UID)
            .build()

        messagesRequest.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage?>>() {
            override fun onSuccess(list: List<BaseMessage?>) {
                instance.getChatHistory(list)
               /*  for (message in list) {
                    if (message is TextMessage) {
                        Log.d("TAG", "Text message received successfully: $message")
                    } else if (message is MediaMessage) {
                        Log.d("TAG", "Media message received successfully: $message")
                    }
                }*/
            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "Message fetching failed with exception: " + e.message)
            }
        })
    }

    fun sendMessage(id: String, message: String,instance: CometChatInterface) {
          val receiverID = id
          val messageText = message
          val receiverType = CometChatConstants.RECEIVER_TYPE_USER

        val textMessage = TextMessage(receiverID, messageText, receiverType)
        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(textMessage: TextMessage) {
                Log.d("TAG", "Message sent successfully: $textMessage")
                instance.onSendMessageSuccessfully(textMessage)
            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "Message sending failed with exception: " + e.message)
            }
        })
    }

    fun messageReciver(instance: CometChatInterface) {
        val listenerID:String = "ChatActivity"

        CometChat.addMessageListener(listenerID, object : CometChat.MessageListener() {
            override fun onTextMessageReceived(textMessage: TextMessage) {
                Log.d("TAG", "Text message received successfully: $textMessage")
                instance.onTextMessageReceived(textMessage)
            }

            override fun onMediaMessageReceived(mediaMessage: MediaMessage) {
                Log.d("TAG", "Media message received successfully: $mediaMessage")
                instance.onMediaMessageReceived(mediaMessage)
            }

            override fun onCustomMessageReceived(customMessage: CustomMessage) {
                Log.d("TAG", "Custom message received successfully: $customMessage")
                instance.onCustomMessageReceived(customMessage)
            }
        })
    }

    fun onMessageReciverStop(){
        CometChat.removeMessageListener("ChatActivity")
    }

    fun onTypingStart(UID:String ){
        val typingIndicator =TypingIndicator(UID,CometChatConstants.RECEIVER_TYPE_USER)

        CometChat.startTyping(typingIndicator)
    }

    fun onTypingStop(UID:String ){
        val typingIndicator = TypingIndicator(UID,CometChatConstants.RECEIVER_TYPE_USER)
        CometChat.endTyping(typingIndicator)
    }

    fun typingListner(instance: CometChatInterface){
        CometChat.addMessageListener("Listener 1",object :CometChat.MessageListener(){
            override fun onTypingEnded(typingIndicator: TypingIndicator?) {
                Log.d("TAG","onTypingEnded: ${typingIndicator.toString()}")
                instance.onTypingStop()
            }

            override fun onTypingStarted(typingIndicator: TypingIndicator?) {
                Log.d("TAG","onTypingStarted: ${typingIndicator.toString()}")
                instance.onTypingStart()
            }

        })
    }

    fun getRecentChat(instance: CometChatInterface){
        var conversationRequest: ConversationsRequest? = null
        val LIMIT:Int=30

        conversationRequest = ConversationsRequest.ConversationsRequestBuilder().setLimit(LIMIT).build()
        conversationRequest?.fetchNext(object : CometChat.CallbackListener<List<Conversation>>() {
            override fun onSuccess(list: List<Conversation>?) {
                instance.onRecentChat(list)
            }

            override fun onError(p0: CometChatException?) {
                //Handle Failure
                Log.d("TAG", "Outgoing call accepted: " )
            }
        })
    }

    fun initCall(uid: String){
        val receiverID = uid
        val receiverType = CometChatConstants.RECEIVER_TYPE_USER
        val callType = CometChatConstants.CALL_TYPE_VIDEO

        val call = Call(receiverID, receiverType, callType)

        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(call: Call) {
                Log.d("TAG", "Call initiated successfully: " + call.toString())
            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "Call initialization failed with exception: " + e.message)
            }
        })
    }

    fun recivedCall(context: CometChatInterface ) {

        val listenerID:String="Call_activity"

        CometChat.addCallListener(listenerID,object :CometChat.CallListener(){
            override fun onOutgoingCallAccepted(p0: Call?) {
                Log.d("TAG", "Outgoing call accepted: " + p0?.toString())
                instance1!!.onStartCall(p0!!.sessionId)
            }
            override fun onIncomingCallReceived(call: Call?) {
                Log.d("TAG", "Incoming call: " + call?.toString())
              //  acceptCall(p0!!.sessionId)
                context.onGotoCall(call?.sessionId)

            }

            override fun onIncomingCallCancelled(p0: Call?) {
                Log.d("TAG", "Incoming call cancelled: " + p0?.toString())
            }

            override fun onOutgoingCallRejected(p0: Call?) {
                Log.d("TAG", "Outgoing call rejected: " + p0?.toString())
            }

        })
    }

    fun acceptCall(sessionId: String) {
        val sessionID:String = sessionId

        CometChat.acceptCall(sessionID,object :CometChat.CallbackListener<Call>(){
            override fun onSuccess(p0: Call?) {
                Log.d("TAG", "Call accepted successfully: " + p0?.toString())
                instance1!!.onStartCall(p0!!.sessionId)

            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG", "Call acceptance failed with exception: " + p0?.message)

            }
        })
    }

    fun onInstance(callActivity: CometChatInterface) {
        instance1 =callActivity
    }



}

interface CometChatInterface{
    fun getAllUserList(list: List<User?>){}
    fun getSingleUserDetails(user: User?){}
    fun getChatHistory(list: List<BaseMessage?>){}
    fun onTextMessageReceived(textMessage: TextMessage) {}
    fun onMediaMessageReceived(mediaMessage: MediaMessage) {}
    fun onCustomMessageReceived(customMessage: CustomMessage) {}
    fun onSendMessageSuccessfully(textMessage: TextMessage) {}
    fun onTypingStart(){}
    fun onTypingStop(){}
    fun onRecentChat(list: List<Conversation>?) {}
    fun onStartCall( sessionId: String?) {}
    fun onGotoCall(sessionId: String?) {}

}
