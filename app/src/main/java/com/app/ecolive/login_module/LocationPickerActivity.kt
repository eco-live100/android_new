package com.app.ecolive.login_module

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.BuildConfig
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityLocationPickerBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
 import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import io.github.g00fy2.quickie.content.QRContent.*
import java.util.Arrays
import javax.annotation.Nullable


class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {
    val PLACE_API_REQUEST_CODE = 100
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var marker: Marker? = null
    val LOCATION_REQUEST_CODE = 1000
    lateinit var binding: ActivityLocationPickerBinding
    lateinit var googleMap: GoogleMap
    lateinit var handler: Handler
    var isApiCall = false
    var isForSelected =false

    //lateinit var mapView:View
    var lastlocation: Location? = null
    var lastAddress: String = ""
    var lastAddressTitle: String = ""

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult == null) {
                return
            } else {
                for (location in locationResult.locations) {
                    Log.d("LOCATION", "" + location)
                    if (location != null) {
                        googleMap.clear()
                        marker = googleMap.addMarker(
                            MarkerOptions().flat(true)
                                .icon(BitmapDescriptorFactory.defaultMarker()).position(
                                    LatLng(location.latitude, location.longitude)
                                )
                        )
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), 16.5F
                            )
                        )
                        binding.myLocationEdt.setText("")
                        lastlocation =location


                        reverseApi(location.latitude, location.longitude)

                        //getAddressText(location.latitude,location.longitude)
                        /* val api =AppleMaps(authToken = resources.getString(R.string.google_maps_key))

                         api.reverseGeocode(location.latitude,location.longitude,"en").let {it->

                         }*/
                    }
                    stopLocationUpdate()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_picker)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment?
        // mapView =mapFragment!!.requireView()
        mapFragment!!.getMapAsync(this@LocationPickerActivity)
        Utils.changeStatusColor(this, R.color.color_050D4C)

        if (intent.getStringExtra("Key")=="ForSelect"){
            isForSelected =true
            binding.imgBack.visibility =View.VISIBLE
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMinUpdateDistanceMeters(10F)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
        placeApiInit()
        handler = Handler(Looper.getMainLooper())
        binding.myLocation.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

//            getLastLocation();
                checkSettingandStartLocationUpdate();
            } else {

                // alertDialog_picker()
            askLocationPermission();
            }


        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.confirmLocation.setOnClickListener {
            if (lastlocation !=null){
                PreferenceKeeper.instance.lastLocationLat =lastlocation!!.latitude.toString()
                PreferenceKeeper.instance.lastLocationLang =lastlocation!!.longitude.toString()
                PreferenceKeeper.instance.lastAddress =lastAddress.toString()
                PreferenceKeeper.instance.lastAddressTitle =lastAddressTitle.toString()
                MyApp.locationLast =lastlocation
                MyApp.lastLocationAddress =lastAddress
                MyApp.lastLocationAddresstitle =lastAddressTitle
                MyApp.locationLast =lastlocation
                if (isForSelected){
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", true)
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }else{
                    startActivity(Intent(this@LocationPickerActivity, UserHomePageNavigationActivity::class.java))
                    finish()
                }

            }else{
                Toast.makeText(this, "Please select a location", Toast.LENGTH_LONG).show()

            }

        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.style)
        googleMap.setMapStyle(mapStyleOptions)
        googleMap.isBuildingsEnabled = true


        googleMap.setOnCameraMoveListener {
            val midLatLng = googleMap.cameraPosition.target
            if (marker != null) {
                marker!!.position = midLatLng
                // nowLocation = marker!!.position
                if (!isApiCall) {
                    isApiCall = true
                    handler.postDelayed({
                        reverseApi(midLatLng.latitude, midLatLng.longitude)
                    }, 3000)
                }
                // getAddressText(midLatLng.latitude,midLatLng.longitude)
            }
        }


    }


    private fun checkSettingandStartLocationUpdate() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!).build()
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener { //setting of devices setisfye
            startLocationUpdate()
//            getLastLocation()

        }
        locationSettingsResponseTask.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, 1001)
                } catch (ex: IntentSender.SendIntentException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun
            startLocationUpdate() {
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            null
        )
    }

    private fun stopLocationUpdate() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdate()
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

//            getLastLocation();
            checkSettingandStartLocationUpdate();
        } else {

            // alertDialog_picker()
            askLocationPermission();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_API_REQUEST_CODE && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.addressTitle.text = place.name
            binding.fullAddress.text = place.address
            binding.llAddress.visibility = View.VISIBLE
            binding.shimmerAddress.visibility = View.GONE
            binding.myLocationEdt.setText(place.address)

            val temp = Location(LocationManager.GPS_PROVIDER)
            temp.latitude = place.latLng!!.latitude
            temp.longitude =place.latLng!!.longitude
            lastlocation=temp
            lastAddress =place.address!!
            lastAddressTitle =place.name!!

            googleMap.clear()
            marker = null

            if (marker == null) {

                marker = googleMap.addMarker(
                    MarkerOptions().flat(true).icon(BitmapDescriptorFactory.defaultMarker())
                        .position(
                            LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                        )
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 16.5F))

            } else {
                marker?.position = place.latLng!!
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 16.5F))
            }


        }

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                startLocationUpdate()
            }
        }

    }


    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    LOCATION_REQUEST_CODE
                )
            } else {


                Snackbar.make(
                    binding.constrant, "Location permission denied",
                    Snackbar.LENGTH_LONG
                ).setAction("Setting") {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri: Uri = Uri.fromParts(
                        "package", this@LocationPickerActivity.getPackageName(),
                        null
                    )
                    intent.data = uri
                    this@LocationPickerActivity.startActivity(intent)
                    binding.constrant
                }.show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
                checkSettingandStartLocationUpdate()
            } else {
                askLocationPermission()
            }
        }
    }

    private fun placeApiInit() {

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        binding.myLocationEdt.setOnClickListener(View.OnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this@LocationPickerActivity)

            startActivityForResult(intent, PLACE_API_REQUEST_CODE)
        })

    }

    private fun reverseApi(latitude: Double, longitude: Double) {
        val latlng: String = latitude.toString() + "," + longitude.toString()
        val map: HashMap<String?, String?> = HashMap()
        map.put("latlng", "" + latlng)
        map.put("key", "" + BuildConfig.MAPS_API_KEY)
        var revrseModel = CommonViewModel(this)
        revrseModel.reverseApi(map).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {


                    if (it.data!!.results.size > 0) {
                        binding.fullAddress.text = it.data.results[0].formatted_address
                        binding.addressTitle.text =
                            it.data.results[0].address_components[0].short_name
                        binding.llAddress.visibility = View.VISIBLE
                        binding.shimmerAddress.visibility = View.GONE
                        binding.myLocationEdt.setText("")
                        lastAddress =it.data.results[0].formatted_address
                        lastAddressTitle =  it.data.results[0].address_components[0].short_name
                    }
                    isApiCall = false


                }

                Status.LOADING -> {
                    isApiCall = true
                    binding.shimmerAddress.visibility = View.VISIBLE
                    binding.llAddress.visibility = View.GONE
                }

                Status.ERROR -> {

                }
            }
        }
    }
}