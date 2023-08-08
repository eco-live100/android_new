package com.app.ecolive.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class CurrentLocation(activity: Activity,onLocationUpdate: OnLocationUpdate)  {
    private   var locationCallback: LocationCallback
    private   var mFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    var activity:Activity?=null
    var onLocationUpdate:OnLocationUpdate?=null


    init {
        this.activity=activity
        this.onLocationUpdate=onLocationUpdate
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

          locationRequest = LocationRequest.Builder(
              Priority.PRIORITY_HIGH_ACCURACY, 4000
          ).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    onLocationUpdate!!.lastLocation(location)
                    //MyApp.location =location
                    }

                }
            }
        }

      fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
             activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

      fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

      fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            123
        )
    }

    @SuppressLint("MissingSuperCall")

       fun startLocationUpdates() {
         if (ActivityCompat.checkSelfPermission(
                 activity!!.applicationContext,
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 activity!!.applicationContext,
                 Manifest.permission.ACCESS_COARSE_LOCATION
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
         }
         locationRequest?.let {
             mFusedLocationClient.requestLocationUpdates(
                 it,
                 locationCallback,
                 Looper.getMainLooper()
             )
         }
     }

       fun stopLocationUpdates() {
         mFusedLocationClient.removeLocationUpdates(locationCallback)
     }

     @SuppressLint("MissingPermission", "SetTextI18n")
       fun getLocation() {
         if (checkPermissions()) {
             if (isLocationEnabled()) {
                 mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
                     val location: Location? = task.result
                     if (location != null) {
                         onLocationUpdate!!.lastLocation(location)
                     }
                 }
             } else {
                 Toast.makeText(  activity!!.applicationContext, "Please turn on location", Toast.LENGTH_LONG).show()
                 val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                 intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK
                 activity!!.applicationContext.startActivity(intent)
             }
         } else {
             requestPermissions()
         }
     }

 }

   interface OnLocationUpdate{

    fun onLocationUpdate()
    fun lastLocation(location: Location)
}