package com.app.ecolive.Voip

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.ecolive.R
import com.app.ecolive.msg_module.ChatActivity
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.helpers.CometChatHelper
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.MediaMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MessagingService : FirebaseMessagingService() {
    var count = 0
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val payload =  remoteMessage.data
        val message = CometChatHelper.processMessage(JSONObject(payload["message"]!!))
        val title = payload["title"]
        val alert = payload["alert"]
        Log.e( "onMessageReceived: ", "$payload \n $message \n $title $alert")
        if (message is Call) {
            initiateCallService(message)
        } else {
            count++
            showMessageNotification(message, title!!, alert!!);
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    private fun initiateCallService(call: Call) {
        try {
            var callManager: CallHandler? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("initiateCallService: ",call.toString())
                callManager = CallHandler(applicationContext)
                callManager.init()
                callManager.startIncomingCall(call)
            }
        } catch (e: Exception) {
            Log.e("initiateCallError:","${e.message}" )
            Toast.makeText(applicationContext, "Unable to receive call due to " + e.message, Toast.LENGTH_LONG)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showMessageNotification(baseMessage: BaseMessage, title: String, alert: String) {
        val messageIntent = Intent(applicationContext, ChatActivity::class.java)
        messageIntent.putExtra(UIKitConstants.IntentStrings.TYPE, baseMessage.receiverType)
        if (baseMessage.receiverType == CometChatConstants.RECEIVER_TYPE_USER) {
            messageIntent.putExtra(UIKitConstants.IntentStrings.NAME, baseMessage.sender.name)
            messageIntent.putExtra(UIKitConstants.IntentStrings.UID, baseMessage.sender.uid)
            messageIntent.putExtra(UIKitConstants.IntentStrings.AVATAR, baseMessage.sender.avatar)
            messageIntent.putExtra(UIKitConstants.IntentStrings.STATUS, baseMessage.sender.status)
        } else if (baseMessage.receiverType == CometChatConstants.RECEIVER_TYPE_GROUP) {
            messageIntent.putExtra(UIKitConstants.IntentStrings.GUID, (baseMessage.receiver as Group).guid)
            messageIntent.putExtra(UIKitConstants.IntentStrings.NAME, (baseMessage.receiver as Group).name)
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC, (baseMessage.receiver as Group).description)
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE, (baseMessage.receiver as Group).groupType)
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER, (baseMessage.receiver as Group).owner)
            messageIntent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT, (baseMessage.receiver as Group).membersCount)
        }
        val messagePendingIntent = PendingIntent.getActivity(applicationContext,
            100, messageIntent, PendingIntent.FLAG_IMMUTABLE)

       /* val logoDrawable = applicationContext.resources.getDrawable(R.mipmap.ic_launcher,resources.newTheme())
        val bmpLogo = Bitmap.createBitmap(logoDrawable.intrinsicWidth,logoDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpLogo)
        logoDrawable.setBounds(0,0,canvas.width,canvas.height)
        logoDrawable.draw(canvas)*/

      /*  val largeIcon : Bitmap = when {
            baseMessage.sender.avatar!=null && !baseMessage.sender.avatar.equals("null",ignoreCase = true)
            -> getBitmapFromURL(baseMessage.sender.avatar)!!
            else -> bmpLogo
        }*/
        val builder = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(resources.getColor(R.color.colorPrimary))

            .setGroup("GROUP_ID")
            .setContentIntent(messagePendingIntent)

            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
       /* if (baseMessage.type == CometChatConstants.MESSAGE_TYPE_IMAGE) {
            builder.setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(getBitmapFromURL((baseMessage as MediaMessage).attachment.fileUrl)))
        }*/
        val summaryBuilder = NotificationCompat.Builder(this, "2")
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$count messages")
            .setSmallIcon(R.mipmap.appicon_144)
            .setGroup("GROUP_ID")
            .setGroupSummary(true)
        val notificationManager = NotificationManagerCompat.from(this)
       /* if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }*/
        notificationManager.notify(baseMessage.id, builder.build())
        //notificationManager.notify(0, summaryBuilder.build())

    }

    fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}