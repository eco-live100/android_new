package com.nightout.ui.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.SrchlocBottomSheetBinding
import com.app.ecolive.user_module.interfacee.OnSelectOptionListener
import com.app.ecolive.utils.Utils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


class SearchLocBottomSheet(private val onSelectOptionListener: OnSelectOptionListener, s: String) :
    BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: SrchlocBottomSheetBinding
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var currelocation :LatLng ? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.srchloc_bottom_sheet, container, false)
        initView()
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun initView() {
        binding.locCloseBtn.setOnClickListener(this)
        binding.confirmLocation.setOnClickListener(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(10)
            .setMaxUpdateDelayMillis(10)
            .build()
        placeApiInit()


        binding.myLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
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


    }

    private fun placeApiInit() {

        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
        binding.PlaceSearch.setOnClickListener(View.OnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fieldList
            ).build(requireContext())

            startActivityForResult(intent, 101)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            "TAG>>>",
            "onActivityResult: " + requestCode
        )
        if (requestCode == 101 && resultCode == RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            currelocation =place.latLng
            binding.PlaceSearch.setText(place.address)


        }
    }


    override fun onClick(v: View?) {

        if (v == binding.locCloseBtn) {
            dismiss()
        }

        else if(v==binding.confirmLocation){
            if (currelocation!=null){
                onSelectOptionListener.onOptionSelect(currelocation.toString())
                dismiss()
            }else{
                Toast.makeText(requireContext(), "Please select location", Toast.LENGTH_SHORT).show()
            }
            
          }
    }

    private fun checkSettingandStartLocationUpdate() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!).build()
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener { //setting of devices setisfye
            startLocationUpdate()
//            getLastLocation()

        }
        locationSettingsResponseTask.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), 1001)
                } catch (ex: IntentSender.SendIntentException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun stopLocationUpdate() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }


    private fun startLocationUpdate() {
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            null
        )
    }

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult == null) {
                return
            } else {
                for (location in locationResult.locations) {

                    if (location != null) {
                        var latLng =LatLng(location.latitude,location.longitude)
                        currelocation =latLng
                        ReverseGeocodingApi(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                        stopLocationUpdate()
                    }
                }
            }
        }
    }

    private fun askLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1003
        )
    }
    private fun ReverseGeocodingApi(lat: String, lang: String) {
        //initiate the service
        val latlng: String = lat + "," + lang
        val map: HashMap<String?, String?> = HashMap()
        map.put("latlng", "" + latlng)
        map.put("key", "" + resources.getString(R.string.google_maps_key))

    }

}

