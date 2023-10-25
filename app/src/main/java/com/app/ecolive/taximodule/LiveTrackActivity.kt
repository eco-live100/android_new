package com.app.ecolive.taximodule

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.ecolive.databinding.ActivityLiveTrackBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
//import com.tbruyelle.rxpermissions2.RxPermissions
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import com.app.ecolive.R

class LiveTrackActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val TAG = "LiveTrackActivity"
    }

    //private val assignedOrder by lazy { intent.getSerializableExtra("assignedOrder") as AssignedOrder<ItemDetails> }
    private val trackUser by lazy { intent.getBooleanExtra("userTrack", false) }

   /* private val destLng: String by lazy {
        if (trackUser)  assignedOrder.seller_long  else assignedOrder.pickup_long
    }

    private val destLat: String by lazy {
        if (trackUser) assignedOrder.seller_lat  else assignedOrder.pickup_lat
    }*/

  /*  private val mobile: String by lazy {
        if (trackUser) assignedOrder.user_mobile else assignedOrder.pickup_mobile
    }*/

    lateinit var binding: ActivityLiveTrackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_live_track)

        //binding.titleTv.text = if (trackUser) "assignedOrder.seller_address"  else "assignedOrder.pickup_name"
        binding.titleTv.text = "From Address"
        //binding.subTitleTv.text = if (trackUser) assignedOrder.seller_name else  assignedOrder.pickup_address
        binding.subTitleTv.text = "To Address"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        binding.call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:7737347444")
            startActivity(intent)
        }

        binding.startNavigation.setOnClickListener {
            // Create a Uri from an intent string. Use the result to create an Intent.
            val gmmIntentUri = Uri.parse("google.navigation:q=26.877019,75.882402")

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
// Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps")

// Attempt to start an activity that can handle the Intent
            startActivity(mapIntent)
        }

        binding.backIv.setOnClickListener { onBackPressed() }
    }


    /**----------------------------------LOCATION SERVICES -----------------------*/

    private var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "onLocationChanged: $location")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.d(TAG, "onStatusChanged: $provider $status $extras")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d(TAG, "onProviderEnabled: $provider")
        }

        override fun onProviderDisabled(provider: String) {
            Log.d(TAG, "onProviderDisabled: $provider")
        }
    }

    private var mMap: GoogleMap? = null

    @SuppressLint("MissingPermission", "CheckResult")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap?.addMarker(
                MarkerOptions().draggable(false).position(
                        LatLng(26.912160, 26.912160)).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_location))
        )


        //mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        checkPermissions()
    }


    @SuppressLint("CheckResult")
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { //check for permission
            /*RxPermissions(this).request(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ).subscribe { isGranted ->
                if (isGranted) {
                    checkLocationSetting()
                } else {
                    Toast.makeText(this,"You can't go further without permission.",Toast.LENGTH_SHORT).show()
                    checkPermissions()
                }
            }*/
        } else { //Permission already granted
            checkLocationSetting()
        }
    }


    private var mLocationPermissionGranted: Boolean = false

    private val requestCheckLocationSetting: Int = 199

    private fun checkLocationSetting() {
        val locationRequest = LocationRequest.create().apply {
            this.interval = 30000
            this.fastestInterval = 5000
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build()
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        task.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                //Permission granted Do fetch location
                mLocationPermissionGranted = true
                getDeviceLocation()
            } catch (exception: ApiException) {
                //Location setting is not enabled show Location Dialog
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the LOCATION  dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(this, requestCheckLocationSetting)
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }// Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCheckLocationSetting && resultCode == Activity.RESULT_OK) {
            mLocationPermissionGranted = true
            getDeviceLocation()
        } else {/// else show the message and show dialog again
            checkLocationSetting()
        }
    }

    private var locationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {

        Log.d("LocationService", "requestLocationUpdates")
        val request = LocationRequest()
        request.interval = 30000
        request.fastestInterval = 5000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.smallestDisplacement = 27f

        val client = LocationServices.getFusedLocationProviderClient(this)
        client.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    //  mMap?.clear()
                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        mMap?.addMarker(
                            MarkerOptions().draggable(false).position(
                                LatLng(
                                    lastLocation.latitude,
                                    lastLocation.longitude
                                )
                            )
                        )


                        if (polyData == null) {
                            fetchPolylines(
                                lastLocation.latitude.toString(),
                                lastLocation.longitude.toString()
                            )
                            mMap?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastLocation.latitude,
                                        lastLocation.longitude
                                    ), 15f
                                )
                            )
                        } else {
                            mMap?.animateCamera(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(
                                        lastLocation.latitude,
                                        lastLocation.longitude
                                    )
                                )
                            )
                            drawPolylines()
                        }
                    }
                }
            }
        }, null)
        //     showProgress()
        //try {
        // val client = LocationServices.getFusedLocationProviderClient(this)
        /* locationResult.addOnSuccessListener { location ->
            if (location != null) {
                ////FINALLY WE HAVE FETCHED THE USER CURRENT LOCATION ....NOW ADD THE MAKER HERE
                //currentLatLng = LatLng(location.latitude, location.longitude)
                //  val geoCoder = Geocoder(this, Locale.getDefault())
                //val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                //   mMap?.addMarker(
                //      MarkerOptions().position(LatLng(location.latitude, location.longitude))
                //           .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                //         .title(city).draggable(false)
                //    )
                //   mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, defaultZoom))

            } else {
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                //mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, defaultZoom))
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
                getDeviceLocation()
            }
        }
    } catch (e: SecurityException) {
        Log.e("Exception: %s", e.message)
    }*/

    }

    private var polyData: List<LatLng>? = null

    fun fetchPolylines(currentLat: String, currentLng: String) {

        val requestQueue = Volley.newRequestQueue(this)
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLat + "," + currentLng +
                "&destination=" + 26.912160 + "," + 26.912160 + "&key=AIzaSyBZeDsuDfoEz3uk6fALVbRzA94fzAbjiWg"

        Log.d(TAG, "URL $url")
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            if (response != null) {
                Log.d("LiveTrackActivity", "POLY LINE RESULT $response")

                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")

                    if (status == "OK") {
                        val routeData = jsonObject.getJSONArray("routes")

                        for (i in 0 until routeData.length()) {
                            val legObject = routeData.get(i) as JSONObject

                            //== Over view poly line gives us the ENCODED form of a POLY string ==//
                            //=============== which we have to decode ===========================//

                            val polyLine = legObject.getString("overview_polyline")

                            val polyEncodeForm = JSONObject(polyLine)
                            val encodedString = polyEncodeForm.getString("points")
                            polyData = decodePoly(encodedString)

                            val legData = legObject.getJSONArray("legs")

                            for (j in 0 until legData.length()) {
                                val distimeObject = legData.get(j) as JSONObject
                                val distance = distimeObject.getString("distance")
                                val time = distimeObject.getString("duration")

                                val distanceObject = JSONObject(distance)
                                //totalDis = distanceObject.getString("text")

                                val timeObject = JSONObject(time)
                                // totalTime = timeObject.getString("text")

                                drawPolylines()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, error.toString())
            //progress_bar.setVisibility(View.GONE)
            println(error)
            // Toast.makeText(this@Home, "Network error occur..!!Please try again", Toast.LENGTH_SHORT).show()
        })

        requestQueue.add(stringRequest)
    }

    fun drawPolylines() {
        if (polyData.isNullOrEmpty()) {
            return
        }


        val polylineOptions = PolylineOptions()
                .width(8f)
                .color(ContextCompat.getColor(this, R.color.darkblue))
                .geodesic(true)

        for (roadPoints in polyData!!.indices) {
            polylineOptions.add(LatLng(polyData!![roadPoints].latitude, polyData!![roadPoints].longitude))
        }

        val polyline = mMap?.addPolyline(polylineOptions)
        polyline?.endCap = RoundCap()
        polyline?.jointType = JointType.ROUND


        // mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLat.toDouble(), currentLng.toDouble()), 15f))
    }
    //================================= POLY LINE ===========================================================//

    //================================ CODE FOR DECODING POLYLINE =======================================//

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }
}