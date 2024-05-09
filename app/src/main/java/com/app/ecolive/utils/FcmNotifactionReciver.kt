package com.app.ecolive.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.ecolive.R
import com.app.ecolive.notification.NotificationModel
import com.app.ecolive.rider_module.HomeRiderActivity
import com.google.android.datatransport.cct.internal.LogResponse.fromJson
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import kotlin.random.Random


class FcmNotifactionReciver : BroadcastReceiver() {
    var context :Context? = null
    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }
    override fun onReceive(ctx: Context?, intent: Intent) {
        context =ctx
        val remoteMessage = intent.getParcelableExtra<RemoteMessage>("remoteMessage")
        //  your custom logic
        Log.d(TAG, "onReceive.remoteMessage.getData: " + remoteMessage!!.data)
        val body: MutableMap<String, String> = remoteMessage.data
        val data = JSONObject(body as Map<*, *>)
        Log.d("jsonsdff", data.toString())
        val notification = fromJson<NotificationModel>(data.toString())
        // Check if message contains a data payload.
        if (data!=null && data.toString().isNotEmpty()) {
            Log.d(ContentValues.TAG, "Message data payload: ${remoteMessage.data}")
            sendNotification(notification)
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                //scheduleJob()
            } else {
                // Handle message within 10 seconds
                //handleNow()
            }
        }
    }
    private fun sendNotification(notification: NotificationModel) {

        Log.d("sendNotification : ", "${notification.userName}")

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val random = Random
        val m: Int = random.nextInt(9999 - 1000) + 1000

        val intent = Intent(context, HomeRiderActivity::class.java).putExtra(AppConstant.notificationModel,notification)
        var intentFlagType = PendingIntent.FLAG_ONE_SHOT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intentFlagType =
                PendingIntent.FLAG_IMMUTABLE // or only use FLAG_MUTABLE >> if it needs to be used with inline replies or bubbles.
        }
        val pendingIntent = TaskStackBuilder.create(context)
            .run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, intentFlagType)
            }

        val notificationBuilder = NotificationCompat.Builder(context!!,
            AppConstant.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.appicon_144)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context!!)

        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstant.NOTIFICATION_CHANNEL_ID,
                AppConstant.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            manager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(m, notificationBuilder)
    }

    companion object {
        private const val TAG = "FcmNotifactionReciver"
    }
}