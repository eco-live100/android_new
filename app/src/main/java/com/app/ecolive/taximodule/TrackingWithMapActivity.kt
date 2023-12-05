package com.app.ecolive.taximodule

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTrackingWithMapBinding
import com.app.ecolive.localmodel.MapData
import com.app.ecolive.taximodule.model.TaxiBookingRequestList
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class TrackingWithMapActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityTrackingWithMapBinding

    // GeeksforGeeks coordinates
    private lateinit var mMap: GoogleMap
    private var originLatitude: Double = 28.5021359
    private var originLongitude: Double = 77.4054901

    // Coordinates of a park nearby
    private var destinationLatitude: Double = 28.5151087
    private var destinationLongitude: Double = 77.3932163
    val user = PreferenceKeeper.instance.loginResponse

    private var vehicleImage: Int? = null
    var destination_n: LatLng? = null
    var src_latlng: LatLng? = null
    var update_src: LatLng? = null
    private val carMarker: Marker? = null
    fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
            Math.atan(lng / lat)
        )
            .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
            Math.atan(lng / lat)
        ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 270).toFloat()
        return (-1).toFloat()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_with_map)
        statusBarColor()


        val trackOrderDetail =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    AppConstant.trackOrderDetail,
                    TaxiBookingRequestList.Data::class.java
                )
            } else {
                intent.getSerializableExtra(AppConstant.trackOrderDetail) as TaxiBookingRequestList.Data
            }
        trackOrderDetail?.let { item ->
            if (item.bookingStatus == "accepted") {
                item.driverLatitude.let { originLatitude = it.toDouble() }
                item.driverLongitude.let { originLongitude = it.toDouble() }
                item.fromLatitude.let { destinationLatitude = it.toDouble() }
                item.fromLongitude.let { destinationLongitude = it.toDouble() }
            } else {
                item.driverLatitude.let { originLatitude = it.toDouble() }
                item.driverLongitude.let { originLongitude = it.toDouble() }
                item.toLatitude.let { destinationLatitude = it.toDouble() }
                item.toLongitude.let { destinationLongitude = it.toDouble() }
            }
        }
        vehicleImage = R.drawable.pick_up_map
        riderTracking()
    }

    private fun riderTracking() {
        val ref = FirebaseDatabase.getInstance().reference.child("EcoLive").child("${user?._id}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        val value1 = dataSnapshot.value as Map<String?, String?>?
                        Log.i("dataSnapshot json", "dataSnapshot" + JSONObject(value1))
                        val jsonObject = JSONObject(value1)
                        Log.d("jsondata", jsonObject.toString() + "")
                        /* if (jsonObject.optString("jobStatus")
                                 .compareTo("Pending", ignoreCase = true) == 0
                         ) {
                             Toast.makeText(
                                 this@TrackingWithMapActivity,
                                 "product not dispatched yet",
                                 Toast.LENGTH_LONG
                             ).show()
                         } else if (jsonObject.optString("jobStatus")
                                 .compareTo("start", ignoreCase = true) == 0
                         ) {*/
                        Log.d("TAG", "Start Trip but AnimateMaker NOt Call")
                        val lat = jsonObject.optString("latitude").toString()
                        val lng = jsonObject.optString("longitude").toString()
                        val riderName = jsonObject.optString("driverName").toString()
                        val driverNumber = jsonObject.optString("driverNumber").toString()
                        val email = jsonObject.optString("email").toString()
                        val timeStamp = jsonObject.optString("timeStamp").toString()
                        binding.riderNameTv.text = "$riderName"
                        originLatitude = lat.toDouble()
                        originLongitude = lng.toDouble()
                        Log.d("TAG", "gsdgsdgffdsg:-$lat----$lng")
                        initView()
                        update_src =
                            LatLng(lat.toDouble(), lng.toDouble())
                        if (!originLatitude.equals(lat) || !originLongitude.equals(lng)) {
                            val location = Location("")
                            location.latitude = lat.toDouble()
                            location.longitude = lng.toDouble()
                            if (TextUtils.isEmpty(
                                    originLatitude.toString()
                                )
                            ) {
                                location.bearing = getBearing(
                                    LatLng(lat.toDouble(), lng.toDouble()),
                                    LatLng(lat.toDouble(), lng.toDouble())
                                )
                            } else {
                                location.bearing = getBearing(
                                    LatLng(
                                        originLatitude,
                                        originLongitude
                                    ),
                                    LatLng(lat.toDouble(), lng.toDouble())
                                )
                            }
                            animateMarker(location, carMarker)
                            Log.d("TAG", "AnimateMaker Call")
                            /*SharedPrefManager.setSharedPrefString(
                                RequestCode.SP_CURRENT_LAT,
                                lat
                            )
                            SharedPrefManager.setSharedPrefString(
                                RequestCode.SP_CURRENT_LONG,
                                lng
                            )*/
                        } else {
                            originLongitude = lat.toDouble()
                            originLongitude = lng.toDouble()
                        }
                        /* } else if (jsonObject.optString("jobStatus").compareTo("end") == 0) {
                             // Callback sample
                            appStateMonitor = AppStateMonitor.create(application)
                             appStateMonitor.addListener(SampleAppStateListener())
                             appStateMonitor.start()
                         }*/
                    } catch (e: java.lang.Exception) {
                        Log.d("exception...", e.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun animateMarker(destination: Location, marker: Marker?) {
        if (carMarker != null && mMap != null) {
            val startPosition: LatLng = carMarker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            val valueAnimator = ValueAnimator.ofFloat(0f, 0.15f)
            valueAnimator.duration = 2000
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { valueAnimator ->
                val v = valueAnimator.animatedFraction
                val lng = v * endPosition.longitude + (1 - v) * startPosition.longitude
                val lat = v * endPosition.latitude + (1 - v) * startPosition.latitude
                val newPos = LatLng(lat, lng)
                val location1 = Location("")
                location1.latitude = startPosition.latitude
                location1.longitude = startPosition.longitude
                val location2 = Location("")
                location2.latitude = newPos.latitude
                location2.longitude = newPos.longitude
                val bear = location1.bearingTo(location2)
                Log.d("distance", location1.distanceTo(location2).toString() + "")
                Log.d("accuracy", location2.accuracy.toString() + "")
                Log.d(
                    "total",
                    (location1.distanceTo(location2) - location2.accuracy).toString() + ""
                )
                Log.d("update_bearing", bear.toString() + "")
                if (location1.distanceTo(location2) - location2.accuracy > 5) {
                    carMarker.setPosition(newPos)
                    carMarker.setAnchor(0.5f, 0.5f)
                    Log.d("update_bearing", bear.toString() + "")
                    carMarker.setRotation(bear)
                    if (mMap != null) {
                        //                            tmMap.animateCamera(CameraUpdateFactory
                        //                                    .newCameraPosition
                        //                                            (new CameraPosition.Builder()
                        //                                                    .target(newPos)
                        ////                                                    .zoom(15.5f)
                        //                                                    .build()));

                        //                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        //                            builder.include(newPos);
                        //                            builder.include(destination_n);
                        //                            LatLngBounds bounds = builder.build();
                        //                            int width = getResources().getDisplayMetrics().widthPixels;
                        //                            int height = getResources().getDisplayMetrics().heightPixels;
                        //                            int padding = (int) (width * 0.0);
                        //                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,height,width,  50);
                        //
                        //                            //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(newPos, 50);
                        //                            tmMap.animateCamera(cu);
                    }
                }
            }
            valueAnimator.start()
        } else {
//            Toast.makeText(Tracking.this, "map null", Toast.LENGTH_SHORT).show();
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun initView() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyAsq0ZEcqknyENt9moynumCdWENgfW_4NQ")
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val originLocation2 = LatLng(originLatitude, originLongitude)
        mMap.clear()


        mMap.addMarker(
            MarkerOptions().position(originLocation2)
                .icon(vehicleImage?.let { bitmapFromVector(applicationContext, it) })
        )
        val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
        mMap.addMarker(
            MarkerOptions().position(destinationLocation)
                .icon(bitmapFromVector(applicationContext, R.drawable.home_pin))
        )
        val url = getDirectionURL(
            originLocation2,
            destinationLocation,
            "AIzaSyAsq0ZEcqknyENt9moynumCdWENgfW_4NQ"
        )
        if (MyApp.isConnectingToInternet(this@TrackingWithMapActivity)) {
            GetDirection(url).execute()
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation2, 18F))
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(15f)
                lineoption.color(resources.getColor(R.color.color_050D4C))
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
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
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}