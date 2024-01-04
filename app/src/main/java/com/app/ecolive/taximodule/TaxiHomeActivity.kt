package com.app.ecolive.taximodule

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTaxiHomeBinding
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.fragment.ActivityFragment
import com.app.ecolive.taximodule.fragment.FragmentSchudle
import com.app.ecolive.taximodule.fragment.HomeFragment
import com.app.ecolive.taximodule.model.MainActivityViewModel
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.location.*
import java.util.*

class TaxiHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityTaxiHomeBinding
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.changeStatusTextColor2(this)
//        Utils.changeStatusTextColor(this)this
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_taxi_home)

        viewModel = ViewModelProvider.NewInstanceFactory().create(MainActivityViewModel::class.java)
        if (savedInstanceState == null) {
            val newFragment: Fragment = HomeFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, newFragment)
                .commit()
        }

        binding.activityll.setOnClickListener {
            val newFragment: Fragment = ActivityFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, newFragment)
                .commit()
        }
        binding.homell.setOnClickListener {
            val newFragment: Fragment = HomeFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, newFragment)
                .commit()
        }

        binding.schudlell.setOnClickListener {
            val newFragment: Fragment = FragmentSchudle()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, newFragment)
                .commit()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create();
        locationRequest!!.interval = 4000;
        locationRequest!!.fastestInterval = 2000;
        locationRequest!!.priority = Priority.PRIORITY_HIGH_ACCURACY;
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...

                    MyApp.locationLast = location
                    Log.d(
                        "TAG",
                        "TaxHome onLocationResult: " + location.latitude + "\n" + location.longitude
                    )
                }
            }
        }

        viewModel.getLocationInformation.observe(this) {
            it?.let {
                when (it.status) {
                    Status.ERROR -> {
                        MyApp.lastLocationAddress = "Location not found!"
                        //binding.addressEditText.setText()
                    }

                    Status.LOADING -> {
                        MyApp.lastLocationAddress =  "Searching..."
                    }

                    Status.SUCCESS -> {
                        it.data?.let { model ->
                            //  binding.addressEditText.setText(model.locationAddress)
                            MyApp.lastLocationAddress = model.locationAddress
                        }
                    }
                }
            }
        }

    }


    override fun onStart() {
        super.onStart()
        getLocation()
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        val location: Location? = task.result
                        if (location != null) {
                            Log.d(
                                "TAG",
                                "TaxiHomeActivity isSuccessful: " + location.latitude + "\n" + location.longitude
                            )
                            viewModel.getLocationInfo(
                                this,
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                           // getAddress(location.latitude, location.longitude)
                           // MyApp.lastLocationAddress = getCompleteAddressString(location.latitude, location.longitude)
                        }
                    }else{
                        Log.d(
                            "TAG","TaxiHomeActivity task ${task.result}}")
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


    private fun getAddress(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.ENGLISH)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latitude, longitude, 1
                ) { addresses ->
                    Log.d(
                        "TAG","asdfsafsadf1 ${addresses[0].getAddressLine(0)}")
                        MyApp.lastLocationAddress = addresses[0].getAddressLine(0).toString()
                    Log.d("TAG","asdfsafsadf2 ${MyApp.lastLocationAddress}")
                }
            } else {
                 val list: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                 MyApp.lastLocationAddress = list?.get(0)?.getAddressLine(0).toString()
                 Log.d("TAG","asdfsafsadf3 ${MyApp.lastLocationAddress}")
             }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationRequest?.let {
                    mFusedLocationClient.requestLocationUpdates(
                        it,
                        locationCallback,
                        Looper.getMainLooper()
                    )
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

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }


}