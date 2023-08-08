package com.app.ecolive.taximodule

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import org.json.JSONObject
import java.util.ArrayList

class VehicalListActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityVehicalListBinding
    var startLat: String? = null
    var startLang: String? = null
    var endLat: String? = null
    var endLang: String? = null
    var polyline: Polyline? = null
    lateinit var startLatLng:LatLng
    lateinit var endLatLng:LatLng
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vehical_list)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)

        if (intent != null) {
            startLat = intent.getStringExtra("STARTLat")
            startLang = intent.getStringExtra("STARTLang")
            endLat = intent.getStringExtra("ENDLat")
            endLang = intent.getStringExtra("ENDLang")
        }
        startLatLng = LatLng(startLat!!.toDouble(), startLang!!.toDouble())
          endLatLng = LatLng(endLat!!.toDouble(), endLang!!.toDouble())
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.taximap2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        binding.paymentOption.setOnClickListener {
            startActivity(Intent(this, TaxiPaymentActivity::class.java))
        }
        getVehicalApi()
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


        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,   50));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 10F))
        if (mMap != null) {
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
                            polyline = mMap!!.addPolyline(
                                DirectionConverter.createPolyline(
                                    this@VehicalListActivity,
                                    directionPositionList,
                                    3,
                                    Color.BLACK
                                )
                            )
                        } else {
                            polyline!!.remove()
                            polyline = mMap!!.addPolyline(
                                DirectionConverter.createPolyline(
                                    this@VehicalListActivity,
                                    directionPositionList,
                                    3,
                                    Color.BLACK
                                )
                            )
                        }

                    }

                    override fun onDirectionFailure(t: Throwable) {
                        Log.d("TAG", "onDirectionSuccess: " + t.message.toString())
                    }
                })
        }
    }

    private fun getVehicalApi() {
        progressDialog.show(this)
        var viewModel = TaxiViewModel(this)
        var json = JSONObject()

        viewModel.getVehicalApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                    var adapter =VehicleAdapter(this,it.data)
                        var layoutManager =LinearLayoutManager(this)
                        binding.recycleVehicle.layoutManager =layoutManager
                        binding.recycleVehicle.adapter=adapter


                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
}