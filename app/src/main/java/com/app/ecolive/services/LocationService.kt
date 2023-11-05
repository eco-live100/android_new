package com.app.ecolive.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.ecolive.R
import com.app.ecolive.localmodel.MyCartListModel
import com.app.ecolive.taximodule.model.LocationModel
import com.app.ecolive.utils.PreferenceKeeper
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class LocationService : Service() {

    companion object {
        private val TAG = "LocationService"
        var isServiceStarted = false
    }
   // private val session by lazy { AccountManager.getUserAccount()!! }
    private val dateFormatter by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }


    val ref = FirebaseDatabase.getInstance().getReference("EcoLive")

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val geoFire: GeoFire by lazy { GeoFire(ref) }
    val user= PreferenceKeeper.instance.loginResponse
    override fun onCreate() {
        super.onCreate()
        buildNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        return START_STICKY
    }
    private fun requestLocationUpdates() {
        Log.d("LocationService", "requestLocationUpdates")
        val request = LocationRequest()
        request.interval = 30000
        request.fastestInterval = 5000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.smallestDisplacement = 27f

        val client = LocationServices.getFusedLocationProviderClient(this)
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (locationResult != null) {
                        val location = locationResult.lastLocation!!
                        /*geoFire.setLocation(
                            user?._id , GeoLocation(location.latitude, location.longitude)
                        ) { key, error -> Log.d(TAG, "Location update on Firebase") }*/
                        val timestamp = dateFormatter.format(System.currentTimeMillis())
                        val locationModel = LocationModel(
                            driverId = user?._id,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timeStamp = timestamp)
                            ref.child("location").setValue(locationModel)

                        /*    FirebaseDatabase.getInstance().reference.child("hudibabarider")
                                .child(session.deliveryBoyId).child("l")
                                .setValue(data).addOnSuccessListener {
                                    Log.d(TAG, "Location updated")
                                }.addOnFailureListener {
                                    Log.e(TAG, "Could not update location", it)
                                }*/

                           /*val refStore = FirebaseFirestore.getInstance()
                        refStore.collection("l").document(user?._id!!).set(locationModel)
                               .addOnSuccessListener { Log.d(TAG, "successfully added") }
                               .addOnFailureListener { Log.e(TAG, "Failed adding ") }
                           Log.d(TAG, "added to  to collection ")*/
                    }
                }
            }, null)
        }
    }

    private fun buildNotification() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                createNotificationChannel("my_service", "My Background Service") else ""

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Grocito Rider")
            .setContentText("You are online")
            .setOngoing(true)
            .setSmallIcon(R.drawable.appicon_512) //TODO change it
        startForeground(1, builder.build())
        Log.d(TAG, "Start Forground Services")
        isServiceStarted = true
    }
    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}
