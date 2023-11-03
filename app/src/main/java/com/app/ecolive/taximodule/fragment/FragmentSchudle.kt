package com.app.ecolive.taximodule.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.adevinta.leku.*
import com.app.ecolive.R
import com.app.ecolive.databinding.FragmentSchudleBinding
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class FragmentSchudle : Fragment() {
    lateinit var binding: FragmentSchudleBinding
    val myCalendar: Calendar = Calendar.getInstance()
    var startLocation:LatLng? =null
    var endLocation:LatLng? =null
    var SelectedDate =1
    var SelectedTime =1
    private val progressDialog = CustomProgressDialog()
    val newFragment: Fragment = HomeFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSchudleBinding.inflate(inflater, container, false)
        binding.toolbar.toolbarTitle.text = "Schedule rides for a\nweek/month"
        binding.toolbar.ivBack.visibility =View.GONE
        val sdf = SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH)
        val currentDate = sdf.format(Date())
        System.out.println(" C DATE is  $currentDate")
        binding.fromDate.text =currentDate
        binding.toDate.text =currentDate
        binding.fromDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.time).toString()
        binding.toDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.time).toString()

        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                if(SelectedDate==1){
                    val date = myCalendar.time
                    val formatter = SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH) //or use getDateInstance()
                    val formatedDate = formatter.format(date)
                    binding.fromDate.text =formatedDate
                }else{
                    val date = myCalendar.time
                    val formatter = SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH) //or use getDateInstance()
                    val formatedDate = formatter.format(date)
                    binding.toDate.text =formatedDate
                }

            }

        val myTimeListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                if (view.isShown) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    myCalendar.set(Calendar.MINUTE, minute)
                }
                if(SelectedTime==1){
                    binding.fromDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.time).toString()
                }else{
                    binding.toDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.time).toString()
                }
            }
        binding.calender1.setOnClickListener {
            SelectedDate= 1
            val dialog = DatePickerDialog(
                requireContext(),
                R.style.my_dialog_theme,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))


        }
        binding.calender2.setOnClickListener {
             SelectedDate =2
            var dialog= DatePickerDialog(
                requireContext(), R.style.my_dialog_theme,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))

        }
        binding.timePickerFrom.setOnClickListener {
            SelectedTime =1
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                R.style.my_dialog_theme,
                myTimeListener,
                myCalendar[Calendar.HOUR],
                myCalendar[Calendar.MINUTE],
                false
            )

            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
        }

        binding.timePickerTo.setOnClickListener {
            SelectedTime =2
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                R.style.my_dialog_theme,
                myTimeListener,
                myCalendar[Calendar.HOUR],
                myCalendar[Calendar.MINUTE],
                false
            )

            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))



        }

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
                .build(requireContext())

            startActivityForResult(locationPickerIntent,100)
        }
        binding.startLocation.text = MyApp.lastLocationAddress.toString()
        startLocation = LatLng(MyApp.locationLast!!.latitude,MyApp.locationLast!!.longitude)
        placeApiInit()
        binding.confirmButton.setOnClickListener {
            if(binding.startLocation.text.isEmpty()){
                Toast.makeText(requireContext(), "Select Start Location", Toast.LENGTH_SHORT).show()
            }else if(binding.DestinationLocation.text.isEmpty()){
                Toast.makeText(requireContext(), "Select Destination Location", Toast.LENGTH_SHORT).show()
            }else{
                confirmTaxiApiCall()
            }
        }
        binding.cancelButton.setOnClickListener {
            //parentFragmentManager.popBackStack();
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment,newFragment)?.commit()
        }
        binding.toolbar.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        return binding.root

    }
    private fun placeApiInit() {

        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
        binding.startLocation.setOnClickListener{
            val fieldList: List<Place.Field> =
                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(requireContext())

            startActivityForResult(intent, 111)
        }
        binding.DestinationLocation.setOnClickListener{
            val fieldList: List<Place.Field> =
                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(requireContext())

            startActivityForResult(intent, 222)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("TAG>>>", "onActivityResult: $requestCode")
        if (requestCode == 111 && resultCode == AppCompatActivity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.startLocation.text = place.address
            startLocation =place.latLng
            Log.d("TAG>>>","onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude))
        }
        if (requestCode == 222 && resultCode == AppCompatActivity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            endLocation =place.latLng
            binding.DestinationLocation.text = place.address

            Log.d("TAG>>>","onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude))
            //setMarker

        }

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            binding.DestinationLocation.text = data?.getStringExtra(LOCATION_ADDRESS)
           // var latLng =LatLng(data?.getDoubleExtra(LATITUDE, 0.0)!!,data?.getDoubleExtra(LONGITUDE, 0.0)!!)
           // endLocation =latLng



        }

    }

/*
    private fun scheduleRideApiCall() {
        progressDialog.show(requireContext())
        val scheduleRideViewModel = TaxiViewModel(requireActivity())
        val json = JSONObject()
        json.put("fromLatitude", "${startLocation?.latitude}")
        json.put("fromLongitude","${startLocation?.longitude}")
        //json.put("toLatitude", "Pani pech jaipur")
        json.put("toLatitude", "${endLocation?.latitude}")
        json.put("toLongitude", "${endLocation?.longitude}")
        //json.put("userAddress", "The Raj Vilas Hotel")
        json.put("fromDate", "${binding.fromDate.text.toString()}")
        json.put("toDate", "${binding.toDate.text.toString()}")
        json.put("pickUpTimeFromDestination", "${binding.fromDestinationTime.text.toString()}")
        json.put("pickUpTimeToDestination", "${binding.toDestinationTime.text.toString()}")
        json.put("rideScheduleType", "${binding.toDestinationTime.text.toString()}")
        scheduleRideViewModel.scheduleRideTaxi(json).observe(requireActivity()) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
                        Toast.makeText(requireContext(), "Request Send SuccessFully for schedule weekly/monthly ride.", Toast.LENGTH_SHORT)
                            .show()
                        parentFragmentManager.popBackStack();
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    val msg = it.message?.let { it1 -> JSONObject(it1) }
                    MyApp.popErrorMsg("", "" + msg?.getString("msg"), requireContext())
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
*/

    private fun confirmTaxiApiCall() {
        progressDialog.show(requireContext())
        val confirmViewModel = TaxiViewModel(requireActivity())
        val json = JSONObject()

        json.put("taxiId", "")
        json.put("fromLatitude", "${startLocation?.latitude}")
        json.put("fromLongitude","${startLocation?.longitude}")
        json.put("fromAddress",  binding.startLocation.text.toString())
        json.put("toLatitude", "${endLocation?.latitude}")
        json.put("toLongitude", "${endLocation?.longitude}")
        json.put("toAddress", binding.DestinationLocation.text.toString())
        json.put("fromDate", "${binding.fromDate.text.toString()}")
        json.put("toDate", "${binding.toDate.text.toString()}")
        json.put("pickUpTimeFrom", "${binding.fromDestinationTime.text.toString()}")
        json.put("pickUpTimeTo", "${binding.toDestinationTime.text.toString()}")
        json.put("paymentType", 0)
        json.put("amount", "")
        json.put("distanceInKm", "")
        json.put("bookingType", 2)

        Log.d("Utils", "startAddress=: ${binding.startLocation.text}")
        Log.d("Utils", "endAddress=: ${binding.DestinationLocation.text}")
        confirmViewModel.confirmTaxi(json).observe(requireActivity()) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
                        Toast.makeText(requireContext(), "Taxi Request Send SuccessFully", Toast.LENGTH_SHORT)
                            .show()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment,newFragment)?.commit()
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    val msg = it.message?.let { it1 -> JSONObject(it1) }
                    MyApp.popErrorMsg("", "" + msg?.getString("msg"), requireContext())
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
}