package com.app.ecolive.taximodule

import VolleyApi
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
import android.os.Handler
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
import com.app.ecolive.service.VolleyApiCompleteTask
import com.app.ecolive.taximodule.fragment.ActivityFragment
import com.app.ecolive.taximodule.fragment.FragmentSchudle
import com.app.ecolive.taximodule.fragment.HomeFragment
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class TaxiHomeActivity : AppCompatActivity() , VolleyApiCompleteTask  {
    lateinit var binding :ActivityTaxiHomeBinding
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    var locationLast :Location ?=null
    var lastLocationAddress =""

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.changeStatusTextColor2(this)
//        Utils.changeStatusTextColor(this)this
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_taxi_home)


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
                        // getDirection(LatLng(location.latitude,location.longitude),LatLng(location.latitude,location.longitude))
                            getAddress(location.latitude,location.longitude)
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
    private fun getDirection(update_src: LatLng?,destination_n: LatLng?) {
        if (update_src != null && destination_n != null) {
            val requestApi = Utils.getDirectionsUrl(update_src, destination_n)
            Log.d("TAG", "getDirection requestApi: $requestApi")
            val objectNew: HashMap<String, String> = HashMap()
            VolleyApi(
                this,
                requestApi,
                objectNew,
                this,
                100,
                3
            )
        }
    }

    private fun getAddress(latLng: LatLng) {
        if (latLng != null) {
            val requestApi = Utils.getAddressUrl(latLng)
            Log.d("TAG", "getDirection requestApi: $requestApi")
            val objectNew: HashMap<String, String> = HashMap()
            VolleyApi(
                this,
                requestApi,
                objectNew,
                this,
                101,
                3
            )
        }
    }
    private fun getAddress(latitude: Double, longitude: Double) : String {
        var address = ""
        try {
            var geocoder = Geocoder(this, Locale.ENGLISH)
            if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(
                    latitude, longitude, 1
                ) { addresses ->
                    Handler(mainLooper).post{
                        MyApp.lastLocationAddress = addresses[0].getAddressLine(0).toString()
                    }
                }
            } else {
                val list: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                address = list?.get(0)?.getAddressLine(0).toString()
                MyApp.lastLocationAddress = address
            }
            return address
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return address
    }

/*    private fun getAddress(
        context: Activity, latitude: Double, longitude: Double) {
        var address = ""
        //try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= 33) {
                // declare here the geocodeListener, as it requires Android API 33
                geocoder.getFromLocation(
                    latitude, longitude, 1
                ) { addresses ->
                    if (addresses.isNotEmpty()) {
                        Log.d("Utils", "getAddress13>=: ${addresses[0]}")
                        address = addresses[0].getAddressLine(0).toString()
                        Log.d("Utils", "getAddress13>=: $address")
                        MyApp.lastLocationAddress = address
                    }
                }
            } else {
                val list: MutableList<Address>? =
                    geocoder.getFromLocation(latitude, longitude, 1)
                address = list?.get(0)?.getAddressLine(0).toString()
                MyApp.lastLocationAddress = address
                Log.d("Utils", "getAddress13<=: $address")
                // For Android SDK < 33, the addresses list will be still obtained from the getFromLocation() method
            }
     *//*   } catch (e: java.lang.Exception) {
            e.printStackTrace() // getFromLocation() may sometimes fail
        }*//*
    }*/

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

    override fun onComplete(response: String?, taskcode: Int) {
        Log.d("response", response!!)
        if (taskcode == 100) {
            var jsonObject: JSONObject? = null
            var totalDistance = 0.0
            var totalDuration: Long = 0
            try {
                jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("routes")
                val sie = jsonArray.length()
                for (i in 0 until jsonArray.length()) {
                    val route = jsonArray.getJSONObject(i)
                    val poly = route.getJSONObject("overview_polyline")
                    val polyline = poly.getString("points")

//                        for (i in 0 until jsonArray.length()) {
                    val jLegs = (jsonArray.get(i) as JSONObject).getJSONArray("legs")
//                            for (j in 0 until jLegs.length()) {
                    val legs1 = jLegs.getJSONObject(0)
                    val distance = legs1.getJSONObject("distance")
                    val duration = legs1.getJSONObject("duration")
                    totalDistance += distance.getLong("value")
                    totalDuration += duration.getLong("value")
//                            }
//                        }
                    totalDistance /= 1000
                    totalDuration /= 60
                    totalDistance = Math.round(totalDistance * 10) / 10.0
                    Log.i("Total_distance", "${distance.getString("text")}")
                    Log.i("Total_duration", "${duration.getString("text")}")
                   /* binding.timeDistanceTv.text =
                        "${duration.getString("text")}\nDistance : ${distance.getString("text")}"

                    polyLineList =
                        DirectionsJSONParser.decodePoly(polyline) as List<LatLng?>*/
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            /*val parserTask = ParserTask(this)
            parserTask.execute(response)*/
        }

        if (taskcode == 101) {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("results")
                val sie = jsonArray.length()
                for (i in 0 until jsonArray.length()) {
                    val route = jsonArray.getJSONObject(i)
                    val poly = route.getJSONObject("overview_polyline")
                    val polyline = poly.getString("points")

//                        for (i in 0 until jsonArray.length()) {
                    val jLegs = (jsonArray.get(i) as JSONObject).getJSONArray("legs")
//                            for (j in 0 until jLegs.length()) {
                    val legs1 = jLegs.getJSONObject(0)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            /*val parserTask = ParserTask(this)
            parserTask.execute(response)*/
        }
    }


}