package com.app.ecolive.taximodule

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adevinta.leku.ADDRESS
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LOCATION_ADDRESS
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.app.ecolive.BuildConfig
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityLocationSelectBinding
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.hbb20.CountryCodePicker

class LocationSelectActivity : AppCompatActivity() {
    lateinit var binding :ActivityLocationSelectBinding
    var startLocation:LatLng? =null
    var endLocation:LatLng? =null
    var scheduleRideType = "request"
    var rideDate : String? = null
    var rideTime  : String? = null
    var totalDistance: Double = 0.0
    var totalDuration: Double = 0.0
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
                scheduleRideType="booking"
                binding.scheduleNoteTv.visibility = View.VISIBLE
                binding.scheduleNoteTv.text = "Scheduling this ride for $rideDate pickup time is $rideTime"
            }else{
                binding.scheduleNoteTv.visibility = View.INVISIBLE
                binding.scheduleNoteTv.text = ""
            }
        }


        MyApp.lastLocationAddress?.let { binding.startLocation.text = it}
        MyApp.locationLast?.let {
            startLocation = LatLng(it.latitude,it.longitude)
        }
        Log.d("LocationSelection", "Location_Selection_Start_address :  ${MyApp.lastLocationAddress.toString()}--$startLocation")

        placeApiInit()

        binding.SetDestinationMap.setOnClickListener {

            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(MyApp.locationLast!!.latitude,  MyApp.locationLast!!.longitude)
                .withGeolocApiKey(BuildConfig.MAPS_API_KEY)
                .withGooglePlacesApiKey(BuildConfig.MAPS_API_KEY)
                .withDefaultLocaleSearchZone()
                .shouldReturnOkOnBackPressed()




                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()

                .withSearchBarHidden()
                .build(this)

            startActivityForResult(locationPickerIntent,100)
        }

    }
    private fun placeApiInit() {

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        binding.startLocation.setOnClickListener {
            val fieldList: List<Place.Field> =
                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))

            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this)

            startActivityForResult(intent, 111)
        }
        binding.destinationLocation.setOnClickListener {
            val fieldList: List<Place.Field> =
                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
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
            if(binding.destinationLocation.text.trim().isNotEmpty()&&binding.startLocation.text.trim().isNotEmpty()){
                getdistance(startLocation!!,endLocation!!)


            }

            //setMarker

        }
        if (requestCode == 222 && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            endLocation =place.latLng
            binding.destinationLocation.text = place.address

               if(binding.startLocation.text.trim()!=""){
                   getdistance(startLocation!!,endLocation!!)


               }

            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng?.latitude.toString() + "/ " + place.latLng?.longitude)
            )
            //setMarker

        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
            Log.d("LATITUDE****", latitude.toString())
            val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
            Log.d("LONGITUDE****", longitude.toString())
            val address = data?.getStringExtra(LOCATION_ADDRESS)
            Log.d("ADDRESS****", address.toString())

            val fullAddress = data?.getParcelableExtra<Address>(ADDRESS)
            if (fullAddress != null) {
                Log.d("FULL ADDRESS****", fullAddress.toString())
                binding.destinationLocation.text =  address.toString()
            }
            endLocation = LatLng(latitude!!,longitude!!)

             getdistance(startLocation!!,endLocation!!)




        }

    }

    fun getdistance(startLatLng: LatLng,endLatLng:LatLng): Boolean {
        var status =false
        GoogleDirection.withServerKey(BuildConfig.MAPS_API_KEY)
            .from(startLatLng)
            .to(endLatLng)
            .avoid(AvoidType.FERRIES)
            .alternativeRoute(false)
            .transportMode(TransportMode.DRIVING)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?) {

                    try {


                        Log.d("TAG", "onDirectionSuccess: " + direction!!.routeList)
                        if(direction.routeList.size<1){
                            Toast.makeText(this@LocationSelectActivity,"Please select location of your city", Toast.LENGTH_SHORT).show()
                            status =false
                            return
                        }
                        val directionPositionList: ArrayList<LatLng> =
                            direction.routeList[0].legList[0].directionPoint

                        if (directionPositionList.isNullOrEmpty()) {
                            Toast.makeText(
                                this@LocationSelectActivity,
                                "Route not found",
                                Toast.LENGTH_SHORT
                            ).show()
                            status =false
                            return
                        }



                        val routeArray = direction.routeList
                        for (i in 0 until routeArray.size) {
                            val legs1 = direction.routeList[i].legList[0]
                            Log.d("TAG", "safsafsafsadgsaasf: ")
                            val distance = legs1.distance
                            val duration = legs1.duration
                            totalDistance += distance.value
                            totalDuration += duration.value
                            totalDistance /= 1000
                            totalDuration /= 60
                            totalDistance = (totalDistance * 10) / 10.0
                            Log.d("TAG", "Total_distance:  - ${distance.text}")
                            Log.d("TAG", "Total_duration: - ${duration.text}")
                            if (distance.value>21000){
                                Toast.makeText(
                                    this@LocationSelectActivity,
                                    "We don't serve service more then 20 KM",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }


                            startActivity(Intent(this@LocationSelectActivity,VehicalListActivity::class.java).
                            putExtra("STARTLat",startLocation?.latitude.toString())
                                .putExtra("STARTLang",startLocation?.longitude.toString())
                                .putExtra("ENDLat",endLocation?.latitude.toString())
                                .putExtra("ENDLang",endLocation?.longitude.toString())
                                .putExtra("startAddress",binding.startLocation.text.toString())
                                .putExtra("endAddress",binding.destinationLocation.text.toString())
                                .putExtra("scheduleRideType",scheduleRideType)
                                .putExtra("scheduleRideDate",rideDate)
                                .putExtra("scheduleRideTime",rideTime)
                            )
                            binding.destinationLocation.text =""
                        }


                    }catch (e:Exception){
                        status =false
                        Toast.makeText(this@LocationSelectActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onDirectionFailure(t: Throwable) {
                    status =false
                    Log.d("TAG", "onDirectionFailure: " + t.message.toString())
                }
            })
        return status
    }
}