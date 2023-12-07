package com.app.ecolive.taximodule

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityVehicalListBinding
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.adapter.VehicleAdapter
import com.app.ecolive.taximodule.model.VehicleModel
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import org.json.JSONObject

class VehicalListActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityVehicalListBinding
    var startLat: String? = null
    var startLang: String? = null
    var endLat: String? = null
    var endLang: String? = null
    var scheduleRideType: String? = null
    var rideDate: String? = null
    var rideTime: String? = null
    var polyline: Polyline? = null
    lateinit var startLatLng: LatLng
    lateinit var endLatLng: LatLng
    var startAddress: String = ""
    var endAddress: String = ""
    private lateinit var vehicleModelData: VehicleModel.Data
    private val progressDialog = CustomProgressDialog()
    var totalDistance: Double = 0.0
    var totalDuration: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {

    /*    window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = window.decorView.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            window.decorView.systemUiVisibility = flags
        }*/
        Utils.fullScreen(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vehical_list)
        mInstance = this
        TaxiPaymentActivity.paymentType = "cash"
        if (intent != null) {
            startLat = intent.getStringExtra("STARTLat")
            startLang = intent.getStringExtra("STARTLang")
            endLat = intent.getStringExtra("ENDLat")
            endLang = intent.getStringExtra("ENDLang")
            startAddress = intent.getStringExtra("startAddress").toString()
            endAddress = intent.getStringExtra("endAddress").toString()
            rideDate = intent.getStringExtra("scheduleRideDate")
            rideTime = intent.getStringExtra("scheduleRideTime")
            scheduleRideType = intent.getStringExtra("scheduleRideType")
            startLatLng = LatLng(startLat!!.toDouble(), startLang!!.toDouble())
            endLatLng = LatLng(endLat!!.toDouble(), endLang!!.toDouble())
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.taximap2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        binding.paymentOption.visibility = View.GONE
        binding.timeDistanceTotalTv.visibility = View.GONE
        binding.taxiConfirmButton.visibility = View.GONE
        binding.paymentOption.setOnClickListener {
            startActivity(Intent(this, TaxiPaymentActivity::class.java))
        }
        binding.taxiConfirmButton.setOnClickListener {
            confirmTaxiApiCall()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.paymentModeTv.text  = if(TaxiPaymentActivity.paymentType == "cash") "Cash -" else "Eco-live wallet -"
    }

    override fun onMapReady(mMap: GoogleMap) {
        /* val latLng = LatLng(28.4747789, 77.0419619)
         ;*/
        val builder = LatLngBounds.Builder()

        builder.include(startLatLng)
        builder.include(endLatLng)

        val bounds = builder.build()

        val zoomWidth = resources.displayMetrics.widthPixels
        val zoomHeight = resources.displayMetrics.heightPixels
        val zoomPadding =
            (zoomWidth * 0.15).toInt() // offset from edges of the map 12% of screen


//                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, zoomHeight, zoomWidth, zoomPadding));


        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 10F))
        if (mMap != null) {
            mMap.addMarker(
                MarkerOptions()
                    .position(startLatLng) //                .flat(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            mMap.addMarker(
                MarkerOptions()
                    .position(endLatLng) //                .flat(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            GoogleDirection.withServerKey("AIzaSyD0BCXGsMPd1V2hFI7vpJIho07UaUpM2LY")
                .from(startLatLng)
                .to(endLatLng)
                .avoid(AvoidType.FERRIES)
                .alternativeRoute(false)
                .transportMode(TransportMode.DRIVING)
                .execute(object : DirectionCallback {
                    override fun onDirectionSuccess(direction: Direction?) {
                        Log.d("TAG", "onDirectionSuccess: " + direction!!.routeList)
                        val directionPositionList: ArrayList<LatLng> =
                            direction.routeList[0].legList[0].directionPoint

                        if(directionPositionList.isNullOrEmpty()){
                            Toast.makeText(this@VehicalListActivity,"Route not found",Toast.LENGTH_SHORT).show()
                            return
                        }
                        if (polyline == null) {
                            polyline = mMap.addPolyline(
                                DirectionConverter.createPolyline(
                                    this@VehicalListActivity,
                                    directionPositionList,
                                    3,
                                    Color.BLACK
                                )
                            )
                        } else {
                            polyline!!.remove()
                            polyline = mMap.addPolyline(
                                DirectionConverter.createPolyline(
                                    this@VehicalListActivity,
                                    directionPositionList,
                                    3,
                                    Color.BLACK
                                )
                            )
                        }
                        Log.d("TAG", "safsafsafsadgsaasf: ")

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
                            totalDistance= (totalDistance* 10) / 10.0
                            Log.d("TAG","Total_distance:  - ${distance.text}")
                            Log.d("TAG","Total_duration: - ${duration.text}")
                        }
                        binding.timeDistanceTotalTv.text = String.format("Estimated Distance : %.2f km And Time : %.0f mins", totalDistance,totalDuration)
                        getVehicleApi(totalDistance)
                    }

                    override fun onDirectionFailure(t: Throwable) {
                        Log.d("TAG", "onDirectionFailure: " + t.message.toString())
                    }
                })
        }
    }

    private fun getVehicleApi(totalDistance: Double) {
        progressDialog.show(this)
        val viewModel = TaxiViewModel(this)
        val json = JSONObject()

        viewModel.getVehicalApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        val adapter = VehicleAdapter(this, it.data,totalDistance)
                        val layoutManager = LinearLayoutManager(this)
                        binding.recycleVehicle.layoutManager = layoutManager
                        binding.recycleVehicle.adapter = adapter

                        binding.paymentOption.visibility = View.VISIBLE
                        binding.timeDistanceTotalTv.visibility = View.VISIBLE
                        binding.taxiConfirmButton.visibility = View.VISIBLE

                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    val vv = it.message
                    //val msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" +vv, this)
                }
            }
        }
    }

    private fun confirmTaxiApiCall() {
        progressDialog.show(this)
        val confirmViewModel = TaxiViewModel(this)
        val json = JSONObject()

        json.put("taxiId", "${vehicleModelData._id}")
        json.put("fromLatitude", "$startLat")
        json.put("fromLongitude", "$startLang")
        json.put("userLatitude", "$startLat")
        json.put("userLongitude", "$startLang")
        json.put("fromAddress", "$startAddress")
        json.put("toLatitude", "$endLat")
        json.put("toLongitude", "$endLang")
        json.put("toAddress", "$endAddress")
        json.put("fromDate", rideDate ?: "")
        json.put("toDate", "")
        json.put("pickUpTimeFrom", rideTime ?: "")
        json.put("pickUpTimeTo", "")
        json.put("paymentType", if (TaxiPaymentActivity.paymentType == "cash") 0 else 1)
        json.put("amount", String.format("%.2f", (totalDistance * vehicleModelData.amount.toDouble())))
        json.put("distanceInKm", String.format("%.0f", totalDistance))
        json.put("bookingType", if (scheduleRideType == "request") 0 else 1)

        Log.d("Utils", "startAddress=: $startAddress")
        Log.d("Utils", "endAddress=: $endAddress")
        confirmViewModel.confirmTaxi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        var vv = it.data
                        Toast.makeText(this, "Taxi Request Send SuccessFully", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(
                            Intent(
                                this,
                                TaxiHomeActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                        finish()
                    }
                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    //val msg = it.message?.let { it1 -> JSONObject(it1) }
                    MyApp.popErrorMsg("", "" + vv, this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    companion object {
        private var mInstance: VehicalListActivity? = null
    }

    fun getInstance(): VehicalListActivity? {
        return mInstance
    }

    fun selectedVehicleData(vehicleModel: VehicleModel.Data) {
        vehicleModelData = vehicleModel
    }
}