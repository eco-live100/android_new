package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class ChatListModel (
    var img: Int,
    var title: String,
    var subTitle: String,
    var time: String,
    var msgCount: String,
    var isVideo: Boolean,
    var isRead: Boolean,

)
data class UserListModel(var username :String,var id :String,var image :String)