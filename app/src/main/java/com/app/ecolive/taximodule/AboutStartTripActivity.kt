package com.app.ecolive.taximodule

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAboutTostartTripBinding
import com.app.ecolive.service.VolleyApi
import com.app.ecolive.service.VolleyApiCompleteTask
import com.app.ecolive.services.LocationService
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.SquareCap
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan

class AboutStartTripActivity : AppCompatActivity(), VolleyApiCompleteTask, OnMapReadyCallback,ParserTask.ParserCallback {
    lateinit var binding: ActivityAboutTostartTripBinding

    private val TAG = "CurrentLocationApp"
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mLocationDatabaseReference: DatabaseReference? = null
    var value_lat: String? = null
    var value_lng: String? = null
    private val dateFormatter by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    val user= PreferenceKeeper.instance.loginResponse
    val ref = FirebaseDatabase.getInstance().getReference("EcoLive")


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private var update_src: LatLng? = null
    private var destination_n: LatLng? = null
    private var lat = 0.0
    private var lng = 0.0
    private var v = 0f
    var handler = Handler()
    var delay = 30 * 1000 //1 second=1000 milisecond, 15*1000=15seconds
    var runnable: Runnable? = null
    var currentLocation = false
    private var routePoints: List<HashMap<String, String>>? = null
    private var polyline: Polyline? = null
    private var blackPolylineOptions: PolylineOptions? = null
    private var blackPolyline: Polyline? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var polyLineList: List<LatLng?> = ArrayList()
    var DEFAULT_ZOOM = 15.5f

    companion object {
        private var carMarker: Marker? = null
        fun getBearing(begin: LatLng?, end: LatLng): Float {
            val lat = abs(begin!!.latitude - end.latitude)
            val lng = abs(begin.longitude - end.longitude)
            if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
                atan(lng / lat)
            )
                .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
                atan(lng / lat)
            ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
                atan(lng / lat)
            ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
                atan(lng / lat)
            ) + 270).toFloat()
            return (-1).toFloat()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_tostart_trip)

        binding.toolbar.toolbarTitle.text = "About to start trip"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.riderMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        destination_n = LatLng(26.912160, 26.912160)



        FirebaseApp.initializeApp(this)
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mLocationDatabaseReference= mFirebaseDatabase?.reference?.child("my current location");

        val serviceStarted = LocationService.isServiceStarted
        if (!serviceStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(
                    Intent(
                        this,
                        LocationService::class.java
                    )
                )
            } else {
                startService(
                    Intent(
                        this,
                        LocationService::class.java
                    )
                )
            }
        }else{
            Log.d(TAG, "onCreate: Location_Service_Started:true1")
        }
        binding.buttonDone.visibility = View.INVISIBLE
        binding.buttonDone.setOnClickListener {
            startActivity(Intent(this,LiveTrackActivity::class.java))
        }

       /* if (!serviceStarted) {
            val onlineStatus = true
            if (onlineStatus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        Intent(
                            this,
                            LocationService::class.java
                        )
                    )
                } else {
                    startService(
                        Intent(
                            this,
                            LocationService::class.java
                        )
                    )
                }
                Log.d(TAG, "onCreate: Location_Service_Started:true")

            }else{
                Log.d(TAG, "onCreate: Location_Service_Started:Online_False")
            }
        } else {
            Log.d(TAG, "onCreate: Location_Service_Started:true1")
        }*/

//        getLocation()
        updateLocationTracking()

    }
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking() {
        val location: Task<Location?> = fusedLocationProviderClient.lastLocation
        location.addOnCompleteListener { task: Task<Location?> ->
            if (task.isSuccessful) {
                val currentLocation = task.result
                if (currentLocation != null) {
                    update_src = LatLng(currentLocation!!.latitude, currentLocation.longitude)
                    print("current_address_and_lati_long  ${update_src} ")
                    update_src = LatLng(currentLocation.latitude, currentLocation.longitude)
                    val builder = LatLngBounds.Builder()
                    builder.include(update_src!!)
                    builder.include(destination_n!!)
                    val bounds = builder.build()
                    val width = resources.displayMetrics.widthPixels
                    val height = resources.displayMetrics.heightPixels
                    val padding = (width * 0.10).toInt()
                    Log.d("dsfasfasd", padding.toString() + "")
                    val cameraUpdate =
                        CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
                    if (mMap != null)
                        carMarker = mMap!!.addMarker(
                            MarkerOptions()
                                .position(update_src!!)
                                .flat(true)
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        Utils.getCarBitmap(
                                            this,
                                            R.drawable.pin_icon
                                        )
                                    )
                                )
                        )
                    mMap?.animateCamera(cameraUpdate)
                }
            } else {
                Utils.Toast(this, "Unable to find current location . Try again later")
            }
        }
        if (TrackingUtility.hasLocationPermissions(this)) {
            val request = LocationRequest().apply {
                interval = AppConstant.LOCATION_UPDATE_INTERVAL
                fastestInterval = AppConstant.FASTEST_LOCATION_UPDATE_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result?.locations?.let { locations ->
                for (location in locations) {
                    Timber.d("New Location ${location.latitude},  ${location.longitude}")
                    update_src = LatLng(location!!.latitude, location.longitude)
                    animateMarker(location, carMarker)
                    Log.d(TAG, "AnimateMaker Call")
                    if (!currentLocation)
                        getDirection()
                }
            }

        }
    }


    fun animateMarker(destination: Location, marker: Marker?) {
        if (carMarker != null && mMap != null) {
            /*new code*/
            val startPosition = carMarker!!.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            val valueAnimator = ValueAnimator.ofFloat(0f, 16f)
            valueAnimator.duration = 5000
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { valueAnimator ->
                v = valueAnimator.animatedFraction
                lng = v * endPosition.longitude + (1 - v) * startPosition.longitude
                lat = v * endPosition.latitude + (1 - v) * startPosition.latitude
                val newPos = LatLng(lat, lng)
//                mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(newPos).zoom(15.5f).build()))
                val location1 = Location("")
                location1.latitude = startPosition.latitude
                location1.longitude = startPosition.longitude
                val location2 = Location("")
                location2.latitude = newPos.latitude
                location2.longitude = newPos.longitude
                val bear = location1.bearingTo(location2)

                if ((location1.distanceTo(location2) - location2.accuracy) > 5) {
                    carMarker!!.position = newPos
                    carMarker!!.setAnchor(0.5f, 0.5f)
                    carMarker!!.rotation = getBearing(startPosition, newPos)
                }

                Log.d("distance", location1.distanceTo(location2).toString() + "")
                Log.d("accuracy", location2.accuracy.toString() + "")
                Log.d("total", (location1.distanceTo(location2) - location2.accuracy).toString())
                Log.d("update_bearing", bear.toString() + "")
            }
            valueAnimator.start()
        } else {
//            Toast.makeText(Tracking.this, "map null", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onResume() {
        //start handler as activity become visible
        handler.postDelayed(Runnable { //
            currentLocation = false
            handler.postDelayed(runnable!!, delay.toLong())
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        currentLocation = false
        handler.removeCallbacks(runnable!!) //stop handler when activity not visible
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onPause()
    }
    override fun onParserResult(
        result: List<List<HashMap<String, String>>>,
        parser: DirectionsJSONParser
    ) {
        for (i in 0 until parser.getRouteList().size) {
            /*if (parser.getRouteList().get(i).getSelectedRoute().equalsIgnoreCase(model.getSelectedRoute())) {
                routePoints = result.get(i);
                showRoute(routePoints);
            }*/
            routePoints = result[i]
            Log.d("routePoints", routePoints.toString() + "")
            showRoute(routePoints)
        }
    }

    private fun showRoute(data: List<HashMap<String, String>>?) {

        mMap!!.clear()

        val lineOptions: PolylineOptions?
        val markerOptions = MarkerOptions()
        val builder = LatLngBounds.Builder()
        builder.include(update_src!!)
        builder.include(destination_n!!)

        val points: ArrayList<LatLng> = ArrayList()
        lineOptions = PolylineOptions()
        for (j in data!!.indices) {
            val point = data[j]
            val lat = point["lat"]!!.toDouble()
            val lng = point["lng"]!!.toDouble()
            val position = LatLng(lat, lng)
            points!!.add(position)
            builder.include(position)
        }

        lineOptions.addAll(points)
        lineOptions.width(6f)
        lineOptions.color(ContextCompat.getColor(this, R.color.color_blue))
        lineOptions.geodesic(true)
        // Drawing polyline in the Google Map for the i-th route
        if (polyline != null)
            polyline!!.remove()
        polyline = mMap!!.addPolyline(lineOptions)

//        LatLngBounds bounds = builder.build();
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.20);
////        if (dis<10000){
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, height, width, padding);
//        mMap.animateCamera(cu);
        blackPolylineOptions = PolylineOptions()
        blackPolylineOptions!!.color(ContextCompat.getColor(this, R.color.darkblue))
        blackPolylineOptions!!.width(12f)
        blackPolylineOptions!!.startCap(SquareCap())
        blackPolylineOptions!!.endCap(SquareCap())
        blackPolylineOptions!!.jointType(JointType.ROUND)
        blackPolylineOptions!!.addAll(polyLineList)
        blackPolyline = mMap!!.addPolyline(blackPolylineOptions!!)

        //Animation
        val polyLineAnimator = ValueAnimator.ofInt(0, 100)
        polyLineAnimator.duration = 10000
        //        polyLineAnimator.setRepeatMode(ValueAnimator.RESTART);
//        polyLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
        polyLineAnimator.interpolator = LinearInterpolator()
        polyLineAnimator.addUpdateListener { valueAnimator ->
            val points = polyline!!.points
            val precentValue = valueAnimator.animatedValue as Int
            val size = points.size
            val newPoints = (size * (precentValue / 100.0f)).toInt()
            val p: kotlin.collections.List<LatLng> =
                points.subList(0, newPoints)
            blackPolyline!!.setPoints(p)
        }
        polyLineAnimator.start()
        if (originMarker != null)
            originMarker!!.remove()
        if (destinationMarker != null)
            destinationMarker!!.remove()
        originMarker = addOriginDestinationMarkerAndGet(polyLineList[0])
        originMarker!!.setAnchor(0.5f, 0.5f)

        mMap!!.addMarker(
            MarkerOptions()
                .position(polyLineList[0]!!)
                .flat(true)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Utils.getCarBitmap(
                            this,
                            R.drawable.pin
                        )
                    )
                )
        )
        destinationMarker = addOriginDestinationMarkerAndGet(polyLineList[polyLineList.size - 1])
        destinationMarker!!.setAnchor(0.5f, 0.5f)
        currentLocation = true

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
            )
            if (!success) {
                Log.e("", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("", "Can't find style. Error: ", e)
        }
        val hueMaker = mMap!!.addMarker(
            MarkerOptions()
                .position(destination_n!!) //                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(destination_n!!, DEFAULT_ZOOM))
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng?): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            originDestinationBitMap
        )
        return mMap!!.addMarker(
            MarkerOptions().position(latLng!!).flat(true).icon(bitmapDescriptor)
        )
    }

    @get:SuppressLint("ResourceAsColor")
    private val originDestinationBitMap: Bitmap
        private get() {
            val height = 35
            val width = 35
            val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = ContextCompat.getColor(this, R.color.darkblue)
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            return bitmap
        }

/*
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        value_lat = java.lang.String.valueOf(location?.latitude)
                        value_lng = java.lang.String.valueOf(location?.longitude)
                        Log.d(TAG, "onConnectedsdfasf: Latitude : $value_lat  &amp; Longitude : $value_lng")
                        val timestamp = dateFormatter.format(System.currentTimeMillis())
                        val locationModel = LocationModel(
                            driverId = user?._id,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timeStamp = timestamp)
                        ref.child("driversList").child(user?._id!!).push().setValue(locationModel)
                       */
/* binding.buttonDone.setOnClickListener {
                            mLocationDatabaseReference?.push()?.setValue("Latitude : $value_lat  &amp; Longitude : $value_lng")
                            Log.d(TAG, "onConnectedsdfasf: Latitude : $value_lat  &amp; Longitude : $value_lng")
                            Toast.makeText(
                                this@AboutStartTripActivity,
                                "Location saved to the Firebasedatabase",
                                Toast.LENGTH_LONG
                            ).show()
                        }*/
    /*

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
*/

    private fun getDirection() {
        if (update_src != null && destination_n != null) {
            val requestApi = Utils.getDirectionsUrl(update_src!!, destination_n!!)
            Log.d(TAG, "getDirection requestApi: $requestApi")
            val objectNew: HashMap<String, String> = HashMap()
            VolleyApi(
                this,
                requestApi,
                objectNew,
                this,
                AppConstant.CODE_Direction_Api,
                3
            )
        }
    }

    override fun onComplete(response: String?, taskcode: Int) {
        Log.d("response", response!!)
        if (taskcode == AppConstant.CODE_Direction_Api) {
            if (response != null) {
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
//                        binding.timeDistanceTv.text =
//                            "${duration.getString("text")}\nDistance : ${distance.getString("text")}"

                        polyLineList =
                            DirectionsJSONParser.decodePoly(polyline) as List<LatLng?>
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val parserTask = ParserTask(this)
                parserTask.execute(response)
            }
        }
    }



}