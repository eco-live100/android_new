package com.app.ecolive.taximodule

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTaxiHomeBinding
import com.app.ecolive.taximodule.fragment.ActivityFragment
import com.app.ecolive.taximodule.fragment.FragmentSchudle
import com.app.ecolive.taximodule.fragment.HomeFragment
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.location.*
import java.util.*

class TaxiHomeActivity : AppCompatActivity()   {
    lateinit var binding :ActivityTaxiHomeBinding
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    var locationLast :Location ?=null
    var lastLocationAddress =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_taxi_home)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)

        if (savedInstanceState == null) {
            val newFragment: Fragment = HomeFragment()
            var far =supportFragmentManager.beginTransaction().replace(R.id.fragment,newFragment).commit()
        }

        binding.activityll.setOnClickListener {
            val newFragment: Fragment = ActivityFragment()
            var far =supportFragmentManager.beginTransaction().replace(R.id.fragment,newFragment).addToBackStack(null).commit()
        }
        binding.homell.setOnClickListener {
            val newFragment: Fragment = HomeFragment()
            var far =supportFragmentManager.beginTransaction().replace(R.id.fragment,newFragment).commit()
        }

        binding.schudlell.setOnClickListener {
            val newFragment: Fragment = FragmentSchudle()
            var far =supportFragmentManager.beginTransaction().replace(R.id.fragment,newFragment).addToBackStack(null).commit()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create();
        locationRequest!!.interval = 4000;
        locationRequest!!.fastestInterval = 2000;
        locationRequest!!.priority = Priority.PRIORITY_HIGH_ACCURACY;
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...

                    MyApp.locationLast =location
                    Log.d(
                        "TAG",
                        "onLocationResult: " + location.latitude + "\n" + location.longitude
                    )
                }
            }
        }
        getLocation()
    }


    override fun onStart() {
        super.onStart()

    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        MyApp.lastLocationAddress = Utils.getAddress(this,location.latitude,location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            123
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }



}