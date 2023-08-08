package com.app.ecolive.user_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityAddAddressBinding
import com.app.ecolive.service.Status
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.json.JSONObject
import java.util.*


class AddAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAddressBinding
    private val progressDialog = CustomProgressDialog()
    var addressType="Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address)
        statusBarColor()
        binding.addressPlacePicker.isActivated =false
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        binding.addressPlacePicker.setOnClickListener {
            startAutocompleteIntent()
        }
        binding.radioButtonHome.setOnClickListener {
            binding.radioButtonHome.setImageResource(R.drawable.ic_radio_selected)
            binding.radioButtonOffice.setImageResource(R.drawable.ic_ration_unselect)
            addressType ="Home"
        }
        binding.radioButtonOffice.setOnClickListener {
            binding.radioButtonHome.setImageResource(R.drawable.ic_ration_unselect)
            binding.radioButtonOffice.setImageResource(R.drawable.ic_radio_selected)
            addressType ="Office"
        }
        binding.save.setOnClickListener {
            if(binding.fullName.text.toString()!=""){
                if (binding.addressPlacePicker.text.toString()!=""){
                    addAddress()
                }else{
                    Toast.makeText(this, "Please add address", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Enter full name ", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun statusBarColor() {
        binding.toolbar.toolbarTitle.text = "Address"
        binding.toolbar.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun startAutocompleteIntent() {
        val fields: List<Place.Field> = Arrays.asList(
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG, Place.Field.VIEWPORT
        )

        // Build the autocomplete intent with field, country, and type filters applied
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.ADDRESS)
            .build(this)
        this.startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                val place = Autocomplete.getPlaceFromIntent(data)
                Log.i("TAG", "Place: " + place.name + ", " + place.id+" "+data)
                binding.addressPlacePicker.setText(place.address.toString())
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun addAddress() {
        progressDialog.show(this)
        var commanViewModel = CommonViewModel(this)
        var json = JSONObject()
        json.put("userId", PreferenceKeeper.instance.loginResponse!!._id)
        json.put("lat", "22.455544")
        json.put("long", "77.8686886")
        json.put("title", binding.addressPlacePicker.text.toString())
        json.put("fullName", binding.fullName.text.toString())
        json.put("addressType", addressType)
        json.put("mobile",  binding.mobieNumber.text.toString())
        commanViewModel.addAddress(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {

                        MyApp.popErrorMsg("", "" + it.message, this)
                        finish()

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


