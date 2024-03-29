package com.app.ecolive.taximodule.fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.adevinta.leku.*
import com.adevinta.leku.locale.SearchZoneRect
import com.app.ecolive.R
import com.app.ecolive.databinding.FragmentHome2Binding
import com.app.ecolive.taximodule.LocationSelectActivity
import com.app.ecolive.taximodule.SavePlaceActivity
import com.app.ecolive.taximodule.SavelocationActivity2
import com.app.ecolive.taximodule.TaxiHomeActivity
import com.app.ecolive.utils.MyApp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentHome2Binding
    val myCalendar: Calendar = Calendar.getInstance()
    val date =
        DatePickerDialog.OnDateSetListener { view, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)

        }

    val myTimeListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            if (view.isShown) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
            }
        }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.taxiMap1) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        binding.WhereToTxt.setOnClickListener {
            startActivity(Intent(requireContext(), LocationSelectActivity::class.java))
        }
        binding.imgArrowSavePlace.setOnClickListener {
            startActivity(Intent(requireContext(), SavePlaceActivity::class.java))
        }

         binding.imgArrowDestinatioOnMAp.setOnClickListener {
          //  startActivity(Intent(requireContext(), SavelocationActivity2::class.java))

        }

        binding.nowImg.setOnClickListener {
            val bottomSheetDialog =  BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.bottom_dialog_schudle);
          val date1=  bottomSheetDialog.findViewById<TextView>(R.id.datetxt)
          val time =  bottomSheetDialog.findViewById<TextView>(R.id.timeTxt)
            bottomSheetDialog.show();
            date1!!.setOnClickListener {
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

            time!!.setOnClickListener {
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
        }




        return binding.root
    }

    override fun onMapReady(mMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled =true
            val latLng = LatLng( MyApp.locationLast!!.latitude,  MyApp.locationLast!!.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

        }else{


        }



    }

}