package com.app.ecolive.taximodule.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adevinta.leku.*
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityNotifactionBinding
import com.app.ecolive.databinding.FragmentSchudleBinding
import com.app.ecolive.taximodule.VehicalListActivity
import com.app.ecolive.utils.MyApp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.SimpleDateFormat
import java.util.*


class FragmentSchudle : Fragment() {
    lateinit var binding: FragmentSchudleBinding
    val myCalendar: Calendar = Calendar.getInstance()
    var startLocation:LatLng? =null
    var endLocation:LatLng? =null
    var SelectedDate =1
    var SelectedTime =1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSchudleBinding.inflate(inflater, container, false)
        binding.toolbar.toolbarTitle.text = "Schedule rides for a\nweek/month"
        binding.toolbar.ivBack.visibility =View.INVISIBLE
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        System.out.println(" C DATE is  "+currentDate)
        binding.fromDate.text =currentDate
        binding.ToDate.text =currentDate
        binding.FromDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.getTime()).toString()
        binding.toDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.getTime()).toString()

        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                if(SelectedDate==1){
                    val date = myCalendar.time
                    val formatter = SimpleDateFormat("dd-MM-yyyy") //or use getDateInstance()
                    val formatedDate = formatter.format(date)
                    binding.fromDate.text =formatedDate
                }else{
                    val date = myCalendar.time
                    val formatter = SimpleDateFormat("dd-MM-yyyy") //or use getDateInstance()
                    val formatedDate = formatter.format(date)
                    binding.ToDate.text =formatedDate
                }

            }

        val myTimeListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                if (view.isShown) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    myCalendar.set(Calendar.MINUTE, minute)
                }
                if(SelectedTime==1){
                    binding.toDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.getTime()).toString()
                }else{
                    binding.FromDestinationTime.text =DateFormat.format("hh:mm aaa",myCalendar.getTime()).toString()
                }
            }
        binding.calender1.setOnClickListener {
            SelectedDate= 1
            var dialog = DatePickerDialog(
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
        binding.timePicker.setOnClickListener {
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

        binding.timePicker2.setOnClickListener {
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
        binding.startLocation.setText(MyApp.lastLocationAddress.toString())
        startLocation = LatLng(MyApp.locationLast!!.latitude,MyApp.locationLast!!.longitude)
        placeApiInit()
        return binding.root

    }
    private fun placeApiInit() {

        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
        binding.startLocation.setOnClickListener(View.OnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(requireContext())

            startActivityForResult(intent, 111)
        })
        binding.DestinationLocation.setOnClickListener(View.OnClickListener {
            val fieldList: List<Place.Field> =
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            //  AutocompleteSupportFragment.newInstance().view?.setBackgroundColor(resources.getColor(R.color.black))
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(requireContext())

            startActivityForResult(intent, 222)
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(
            "TAG>>>",
            "onActivityResult: "+requestCode
        )
        if (requestCode == 111 && resultCode == AppCompatActivity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            binding.startLocation.setText(place.address)
            startLocation =place.latLng
            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude)
            )

            //setMarker

        }
        if (requestCode == 222 && resultCode == AppCompatActivity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)
            endLocation =place.latLng
            binding.DestinationLocation.setText(place.address)

            Log.d(
                "TAG",
                "onActivityResult: " + (place.latLng!!.latitude.toString() + "/ " + place.latLng!!.longitude)
            )
            //setMarker

        }

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            binding.DestinationLocation.setText(data?.getStringExtra(LOCATION_ADDRESS))
           // var latLng =LatLng(data?.getDoubleExtra(LATITUDE, 0.0)!!,data?.getDoubleExtra(LONGITUDE, 0.0)!!)
           // endLocation =latLng



        }

    }
}