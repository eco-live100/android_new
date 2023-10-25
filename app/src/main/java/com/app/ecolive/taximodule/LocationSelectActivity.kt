package com.app.ecolive.taximodule

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityLocationSelectBinding
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class LocationSelectActivity : AppCompatActivity() {
    lateinit var binding :ActivityLocationSelectBinding
    var startLocation:LatLng? =null
    var endLocation:LatLng? =null
    var scheduleRideType = "scheduleNow"
    var rideDate : String? = null
    var rideTime  : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor2(this)
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_location_select)

        binding.toolbar.toolbarTitle.text ="Select a location"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.AddStop.setOnClickListener {
            startActivity(Intent(this,AddStopActivity::class.java))
        }
        if (intent != null) {
            rideDate = intent.getStringExtra("scheduleRideDate")
            rideTime = intent.getStringExtra("scheduleRideTime")
            if (rideDate!=null && rideTime!=null ){
                scheduleRideType="scheduleOnDate"
                binding.scheduleNoteTv.visibility = View.VISIBLE
                binding.scheduleNoteTv.text = "Scheduling this ride for $rideDate pickup time is $rideTime"
            }else{
                binding.scheduleNoteTv.visibility = View.INVISIBLE
                binding.scheduleNoteTv.text = ""
            }
        }

        Log.d("LocationSelection", "Location_Selection_Start_address :  ${MyApp.lastLocationAddress.toString()}")
        binding.startLocation.text = MyApp.lastLocationAddress.toString()
        startLocation =LatLng(MyApp.locationLast!!.latitude,MyApp.locationLast!!.longitude)
        placeApiInit()

        binding.SetDestinationMap.setOnClickListener {

            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(MyApp.locationLast!!.latitude,  MyApp.locationLast!!.longitude)
                .withGeolocApiKey("AIzaSyBLbppC7WO6c-WOD0V_YIocVKoA4NKcE50")
                .withGooglePlacesApiKey("AIzaSyBLbppC7WO6c-WOD0V_YIocVKoA4NKcE50")
                .withDefaultLocaleSearchZone()
                .shouldReturnOkOnBackPressed()
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .withSearchBarHidden()
                .build(this)

            startActivityForResult(locationPickerIntent,100)
        }

    }
    private fun placeApiInit() {

        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        binding.startLocation.setOnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

            startActivityForResult(intent, 111)
        }
        binding.DestinationLocation.setOnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

            startActivityForResult(intent, 222)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(
            "TAG>>>",
            "onActivityResult: "+requestCode
        )
        if (requestCode == 111 && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.startLocation.text =  place.address
            startLocation = place.latLng
            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude)
            )
            if(binding.DestinationLocation.text.trim().isNotEmpty()&&binding.startLocation.text.trim().isNotEmpty()){
                startActivity(Intent(this,VehicalListActivity::class.java).
                putExtra("STARTLat",startLocation!!.latitude.toString())
                    .putExtra("STARTLang",startLocation!!.longitude.toString())
                    .putExtra("ENDLat",endLocation!!.latitude.toString())
                    .putExtra("ENDLang",endLocation!!.longitude.toString())
                    .putExtra("startAddress",binding.startLocation.text.toString())
                    .putExtra("endAddress",binding.DestinationLocation.text.toString())
                    .putExtra("scheduleRideType",scheduleRideType)
                    .putExtra("scheduleRideDate",rideDate)
                    .putExtra("scheduleRideTime",rideTime)
                )
            }
            binding.DestinationLocation.text =""
            //setMarker

        }
        if (requestCode == 222 && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            endLocation =place.latLng
            binding.DestinationLocation.text = place.address
               if(binding.startLocation.text.trim()!=""){
                   startActivity(Intent(this,VehicalListActivity::class.java)
                       .putExtra("STARTLat",startLocation!!.latitude.toString())
                       .putExtra("STARTLang",startLocation!!.longitude.toString())
                       .putExtra("ENDLat",endLocation!!.latitude.toString())
                       .putExtra("ENDLang",endLocation!!.longitude.toString())
                       .putExtra("startAddress",binding.startLocation.text.toString())
                       .putExtra("endAddress",binding.DestinationLocation.text.toString())
                       .putExtra("scheduleRideType",scheduleRideType)
                       .putExtra("scheduleRideDate",rideDate)
                       .putExtra("scheduleRideTime",rideTime))
               }
            binding.DestinationLocation.text =""
            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude)
            )
            //setMarker

        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            startActivity(Intent(this,VehicalListActivity::class.java)
                .putExtra("STARTLat",startLocation!!.latitude.toString())
                .putExtra("STARTLang",startLocation!!.longitude.toString())
                .putExtra("ENDLat",data?.getDoubleExtra(LATITUDE, 0.0).toString())
                .putExtra("ENDLang",data?.getDoubleExtra(LONGITUDE, 0.0).toString())
                .putExtra("startAddress",binding.startLocation.text.toString())
                .putExtra("endAddress",binding.DestinationLocation.text.toString())
                .putExtra("scheduleRideType",scheduleRideType)
                .putExtra("scheduleRideDate",rideDate)
                .putExtra("scheduleRideTime",rideTime)
            )

            binding.DestinationLocation.text =""
        }

    }
}