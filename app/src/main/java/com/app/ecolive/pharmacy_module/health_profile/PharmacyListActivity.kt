package com.app.ecolive.pharmacy_module.health_profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityPharmacyListBinding
import com.app.ecolive.pharmacy_module.PharmacyViewModel.PharmacyViewModel
import com.app.ecolive.pharmacy_module.adapter.PharmacyListAdapter
import com.app.ecolive.pharmacy_module.model.PharmacyData
import com.app.ecolive.service.Status
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import okhttp3.MultipartBody
import java.util.Locale

class PharmacyListActivity : AppCompatActivity()  {
    lateinit var binding: ActivityPharmacyListBinding
    lateinit var pharmacyListAdapter: PharmacyListAdapter
    private var pharmacyList: ArrayList<PharmacyData> = ArrayList()
    private val progressDialog = CustomProgressDialog()

    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null

    companion object {
        var mInstance = PharmacyListActivity()
    }

    var prescriptionId = ""
    var myLocation : Location? = null
    var myAddress : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pharmacy_list)
        binding.toolbar.toolbarTitle.text = getString(R.string.pharmacy_list_eco_live)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        mInstance = this
        intent.extras?.let {
            prescriptionId = it.getString(AppConstant.prescriptionId,"");
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recycleFriends.layoutManager = layoutManager
        pharmacyListAdapter = PharmacyListAdapter(this, pharmacyList)
        binding.recycleFriends.adapter = pharmacyListAdapter

        binding.searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchedKey = binding.searchUserEditText.text.toString()
                pharmacyListAdapter.filter.filter(searchedKey)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest?.interval = 4000
        locationRequest?.fastestInterval = 2000
        locationRequest?.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult
                for (location in locationResult.locations) {
                    MyApp.locationLast = location
                    Log.d(
                        "TAG",
                        "PharmacyList onLocationResult: " + location.latitude + "\n" + location.longitude
                    )
                }
            }
        }
        getLocation()
    }

    private fun getPharmacyListApi(location: Location) {
        progressDialog.show(this)
        val viewModel = PharmacyViewModel(this)

        val lat = location.latitude
        val long = location.longitude
        var distance = 30000
        var page = 1
        var limit = 100

        pharmacyList.clear()
        viewModel.getPharmacyListApi(
            lat = lat,
            long = long,
            distance = distance,
            page = page,
            limit = limit
        ).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        pharmacyList.addAll(it.data.items)
                        pharmacyListAdapter.notifyDataSetChanged()
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    //var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + vv, this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    fun getInstance(): PharmacyListActivity {
        return mInstance
    }

    fun placeOrderApi(pharmacyId: String) {
        try {
            MyApp.hideSoftKeyboard(this)
        } catch (_: Exception) {
        }
        val pharmacyViewModel = PharmacyViewModel(this)
        progressDialog.show(this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("precriptionId", prescriptionId)
        builder.addFormDataPart("pharmacyId", pharmacyId)
        builder.addFormDataPart("lat", myLocation?.latitude.toString())
        builder.addFormDataPart("long", myLocation?.longitude.toString())
        builder.addFormDataPart("address", myAddress.toString())

        pharmacyViewModel.placeOrderApi(builder.build()).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        Utils.showMessage(this, "Order placed successfully")
                        finish()
                    }
                }

                Status.LOADING -> {
                    Log.d("ok", "LOADING: ")
                }

                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    Log.d("ok", "ERROR: ")
                    MyApp.popErrorMsg("", it.message!!, this)

                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        myLocation = location
                        Log.d("TAG", "getLocation: $myLocation")
                        getAddress(location.latitude,location.longitude)
                        getPharmacyListApi(location)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun getAddress(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.ENGLISH)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latitude, longitude, 1
                ) { addresses ->
                    Log.d(
                        "TAG","asdfsafsadf1 ${addresses[0].getAddressLine(0)}")
                    myAddress = addresses[0].getAddressLine(0).toString()
                    Log.d("TAG","asdfsafsadf2 ${myAddress}")
                }
            } else {
                val list: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                myAddress = list?.get(0)?.getAddressLine(0).toString()
                Log.d("TAG","asdfsafsadf3 ${myAddress}")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            123
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationRequest?.let {
            mFusedLocationClient.requestLocationUpdates(
                it,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
}