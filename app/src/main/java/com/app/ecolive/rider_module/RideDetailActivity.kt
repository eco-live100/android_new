package com.app.ecolive.rider_module

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.app.ecolive.R
import com.app.ecolive.databinding.RidedetailActivityBinding
import com.app.ecolive.rider_module.model.RiderOrderModel
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.utils.AppConstant
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


class RideDetailActivity  : AppCompatActivity(), OnMapReadyCallback  {
    lateinit var binding : RidedetailActivityBinding
    private val progressDialog = CustomProgressDialog()

    var polyline: Polyline? = null
    private lateinit var startLatLng: LatLng
    private lateinit var endLatLng: LatLng
    var totalDistance: Double = 0.0
    var totalDuration: Double = 0.0
    private var trackOrderDetail : RiderOrderModel.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@RideDetailActivity,R.layout.ridedetail_activity)
        setToolBar()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.rideDetailMap) as SupportMapFragment?


        trackOrderDetail =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.getSerializable(
                    AppConstant.trackOrderDetail,
                    RiderOrderModel.Data::class.java
                )
            } else {
                intent.extras?.getSerializable(AppConstant.trackOrderDetail) as RiderOrderModel.Data
            }
        binding.startRideButton.visibility = View.GONE
        binding.pDetailComplete.visibility = View.GONE
        trackOrderDetail?.let {
            binding.apply {
                when (it.bookingStatus) {
                    "accepted" -> {
                        startLatLng = LatLng(it.driverLatitude.toDouble(), it.driverLongitude.toDouble())
                        endLatLng = LatLng(it.fromLatitude.toDouble(), it.fromLongitude.toDouble())
                        startRideButton.visibility = View.VISIBLE
                        pDetailComplete.visibility = View.GONE
                        bookingStatusTv.text="${it.bookingStatus}".capitalize()
                        bookingStatusTv.setTextColor(resources.getColor(R.color.color_FF9100))
                    }
                    "started" -> {
                        startLatLng = LatLng(it.fromLatitude.toDouble(), it.fromLongitude.toDouble())
                        endLatLng = LatLng(it.toLatitude.toDouble(), it.toLongitude.toDouble())
                        startRideButton.visibility = View.GONE
                        pDetailComplete.visibility = View.VISIBLE
                        bookingStatusTv.text="${it.bookingStatus}".capitalize()
                        bookingStatusTv.setTextColor(resources.getColor(R.color.color_006400 ))
                    }
                    "completed" -> {
                        startRideButton.visibility = View.GONE
                        pDetailComplete.visibility = View.GONE
                        bookingStatusTv.text="${it.bookingStatus}".capitalize()
                        bookingStatusTv.setTextColor(resources.getColor(R.color.color_red))
                    }
                }
                bookingIdTv.text="Booking Id:- ${it.bookingNumber}"
                bookingDateTv.text="${it.createdAt}"
                pDetailUserName.text="${it.userName.capitalize()}"
                fromAddressTv.text="${it.fromAddress}"
                toAddressTv.text="${it.toAddress}"
                tvTotBill.text="Total Bill:- $ ${it.amount}"
                pDetailUserKm.text="${it.distanceInKm}KM"
            }
            startLatLng = LatLng(it.fromLatitude.toDouble(), it.fromLongitude.toDouble())
            endLatLng = LatLng(it.toLatitude.toDouble(), it.toLongitude.toDouble())

            mapFragment?.getMapAsync(this)
        }
        binding.pDetailComplete.setOnClickListener {
            if(MyApp.driverlocation==null){
                Toast.makeText(this,"Location not found", Toast.LENGTH_SHORT).show()
            }else {
                completeAPI(
                    latitude = MyApp.driverlocation?.latitude.toString(),
                    longitude = MyApp.driverlocation?.longitude.toString()
                )
            }
        }
        binding.startRideButton.setOnClickListener {
            if(MyApp.driverlocation==null){
                Toast.makeText(this,"Location not found", Toast.LENGTH_SHORT).show()
            }else {
                startRideAPI(
                    latitude = MyApp.driverlocation?.latitude.toString(),
                    longitude = MyApp.driverlocation?.longitude.toString()
                )
            }
        }
    }
    private fun setToolBar() {
        binding.toolbarRideDetail.ivBack.setOnClickListener { finish() }
        binding.toolbarRideDetail.toolbarTitle.text= getString(R.string.order_detail)
        Utils.changeStatusColor(this, R.color.color_050D4C)
     //  Utils.changeStatusTextColor(this)
    }

    override fun onMapReady(mMap: GoogleMap) {
/*        startLatLng = LatLng(26.8775, 75.8822254)
        endLatLng = LatLng(26.8947446, 75.8301169)*/
        val builder = LatLngBounds.Builder()

        builder.include(startLatLng)
        builder.include(endLatLng)

        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap.animateCamera(cu)
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
                        if (polyline == null) {
                            polyline = mMap.addPolyline(
                                DirectionConverter.createPolyline(
                                    this@RideDetailActivity,
                                    directionPositionList,
                                    3,
                                    Color.BLACK
                                )
                            )
                        } else {
                            polyline!!.remove()
                            polyline = mMap.addPolyline(
                                DirectionConverter.createPolyline(
                                    this@RideDetailActivity,
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
                      /*  binding.timeDistanceTotalTv.text = String.format("Estimated Distance : %.2f km And Time : %.0f mins", totalDistance,totalDuration)
                        getVehicleApi(totalDistance)*/
                    }

                    override fun onDirectionFailure(t: Throwable) {
                        Log.d("TAG", "onDirectionFailure: " + t.message.toString())
                    }
                })
        }
    }

    private fun startRideAPI(latitude: String, longitude: String) {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "${trackOrderDetail?.bookingNumber}")
        json.put("driverId","${trackOrderDetail?.driverId}")
        json.put("taxiId","${trackOrderDetail?.driverId}")
        json.put("driverLatitude",latitude)
        json.put("driverLongitude",longitude)
        json.put("driverAddress","${trackOrderDetail?.driverAddress}")

        taxiViewModel.startRideRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        val returnIntent = Intent()
                        returnIntent.putExtra("refresh", 1)
                        setResult(RESULT_OK, returnIntent)
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
    private fun completeAPI(latitude: String, longitude: String) {
        var taxiViewModel = TaxiViewModel(this)
        progressDialog.show(this)
        val json = JSONObject()
        json.put("bookingNumber", "${trackOrderDetail?.bookingNumber}")
        json.put("driverId","${trackOrderDetail?.driverId}")
        json.put("taxiId","${trackOrderDetail?.driverId}")
        json.put("driverLatitude",latitude)
        json.put("driverLongitude",longitude)
        json.put("driverAddress","${trackOrderDetail?.driverAddress}")

        taxiViewModel.completeBookingRequestRideApi(json).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        val returnIntent = Intent()
                        returnIntent.putExtra("refresh", 1)
                        setResult(Activity.RESULT_OK, returnIntent);
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
}