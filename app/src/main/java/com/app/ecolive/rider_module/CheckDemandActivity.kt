package com.app.ecolive.rider_module

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ChkdemandActivityBinding
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.offercity.base.BaseActivity


class CheckDemandActivity : BaseActivity(), OnMapReadyCallback {
    lateinit var binding: ChkdemandActivityBinding
    private lateinit var mapFragment: SupportMapFragment
    private var locationButton: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(THIS!!, R.layout.chkdemand_activity)
        setToolBar()
        mapFragment = supportFragmentManager.findFragmentById(R.id.demandMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        binding.demandBackBtn.setOnClickListener {
            finish()
        }
        val user = PreferenceKeeper.instance.loginResponse
        if (user != null) {
            "${user.firstName.replaceFirstChar { it.uppercase() }} ${user.lastName.replaceFirstChar { it.uppercase() }}".also {
                binding.demandUserName.text = it
            }
            //Glide.with(this).load(user.profilePicture).into(binding.include.contentHome.riderUserPic)
        }

        intent.extras?.let { intent ->
            val riderStatus = intent.getBoolean("RiderStatus", false)
            if (riderStatus) {
                binding.demandOnline.text = getString(R.string.online)
                binding.demandOnline.setTextColor(resources.getColor(R.color.color_51E555))
            } else {
                binding.demandOnline.text = getString(R.string.offline)
                binding.demandOnline.setTextColor(resources.getColor(R.color.color_red))
            }
        }
    }

    override fun onMapReady(mMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            mMap.isMyLocationEnabled = true

            locationButton = (mapFragment.requireView().findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            // Change the visibility of my location button
            locationButton?.visibility = View.GONE
            val latLng = MyApp.locationLast?.let { LatLng(it.latitude, it.longitude) }
            latLng?.let {
                binding.myLocation.setOnClickListener {
                    locationButton?.callOnClick()
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
            }
        } else {


        }
    }
}