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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_location_select)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Select a location"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.AddStop.setOnClickListener {
            startActivity(Intent(this,AddStopActivity::class.java))
        }
        binding.startLocation.text =MyApp.lastLocationAddress.toString()
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
        binding.startLocation.setOnClickListener(View.OnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

            startActivityForResult(intent, 111)
        })
        binding.DestinationLocation.setOnClickListener(View.OnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

            startActivityForResult(intent, 222)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(
            "TAG>>>",
            "onActivityResult: "+requestCode
        )
        if (requestCode == 111 && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.startLocation.setText(place.address)
            startLocation =place.latLng
            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude)
            )
            if(binding.DestinationLocation.text.trim()!=""){
                startActivity(Intent(this,VehicalListActivity::class.java).
                putExtra("STARTLat",startLocation!!.latitude.toString())
                    . putExtra("STARTLang",startLocation!!.longitude.toString())
                    .putExtra("ENDLat",endLocation!!.latitude.toString())
                    .putExtra("ENDLang",endLocation!!.longitude.toString()))
            }
            binding.DestinationLocation.text =""
            //setMarker

        }
        if (requestCode == 222 && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            endLocation =place.latLng
            binding.DestinationLocation.setText(place.address)
               if(binding.startLocation.text.trim()!=""){
                   startActivity(Intent(this,VehicalListActivity::class.java).
                   putExtra("STARTLat",startLocation!!.latitude.toString())
                       . putExtra("STARTLang",startLocation!!.longitude.toString())
                       .putExtra("ENDLat",endLocation!!.latitude.toString())
                       .putExtra("ENDLang",endLocation!!.longitude.toString()))
               }
            binding.DestinationLocation.text =""
            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude)
            )
            //setMarker

        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            startActivity(Intent(this,VehicalListActivity::class.java).
            putExtra("STARTLat",startLocation!!.latitude.toString())
                . putExtra("STARTLang",startLocation!!.longitude.toString())
                .putExtra("ENDLat",data?.getDoubleExtra(LATITUDE, 0.0).toString())
                .putExtra("ENDLang",data?.getDoubleExtra(LONGITUDE, 0.0).toString()))

            binding.DestinationLocation.text =""
        }

    }
}