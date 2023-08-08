package com.app.ecolive.user_module

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityTrackingWithMapBinding
import com.app.ecolive.databinding.ActivityTrackingWithProgressBinding
import com.app.ecolive.localmodel.MapData
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class TrackingWithMapActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityTrackingWithMapBinding

    // GeeksforGeeks coordinates
    private lateinit var mMap: GoogleMap
    private var originLatitude: Double = 28.5021359
    private var originLongitude: Double = 77.4054901

    // Coordinates of a park nearby
    private var destinationLatitude: Double = 28.5151087
    private var destinationLongitude: Double = 77.3932163

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_with_map)
        statusBarColor()
        initView()
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


       /* mapFragment.getMapAsync {
            mMap = it
            val originLocation = LatLng(originLatitude, originLongitude)
            mMap.addMarker(
                MarkerOptions().position(originLocation)
                    .icon(BitmapFromVector(applicationContext, R.drawable.home_pin))
            )
            val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
            mMap.addMarker(
                MarkerOptions().position(destinationLocation)
                    .icon(BitmapFromVector(applicationContext, R.drawable.home_pin))
            )
            val urll = getDirectionURL(
                originLocation,
                destinationLocation,
                "AIzaSyAsq0ZEcqknyENt9moynumCdWENgfW_4NQ"
            )
            GetDirection(urll).execute()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))
        }*/

    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
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
        mMap = p0!!
        val originLocation2 = LatLng(originLatitude, originLongitude)
        mMap.clear()


        mMap.addMarker(
            MarkerOptions().position(originLocation2)
                .icon(BitmapFromVector(applicationContext, R.drawable.home_pin))
        )
        val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
        mMap.addMarker(
            MarkerOptions().position(destinationLocation)
                .icon(BitmapFromVector(applicationContext, R.drawable.home_pin))
        )
        val urll = getDirectionURL(
            originLocation2,
            destinationLocation,
            "AIzaSyAsq0ZEcqknyENt9moynumCdWENgfW_4NQ"
        )
        if(MyApp.isConnectingToInternet(this@TrackingWithMapActivity)){
         GetDirection(urll).execute()}


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