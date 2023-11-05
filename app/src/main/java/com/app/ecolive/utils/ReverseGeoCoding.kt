package com.app.ecolive.utils

import android.R
import android.text.TextUtils
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner


class ReverseGeoCoding {
    private var address1: String? = null
    private  var address2:String? = null
    private  var city:String? = null
    private  var state:String? = null
    private  var country:String? = null
    private  var county:String? = null
    private  var PIN:String? = null
    private val LOG_TAG = ReverseGeoCoding::class.java.simpleName

    fun constructor(latitude: Double, longitude: Double) {
        init()
        CoroutineScope(Dispatchers.IO).launch{
            retrieveData(latitude, longitude)
        }
    }

    private fun retrieveData(latitude: Double, longitude: Double){
        try {
            val responseFromHttpUrl: String? = buildUrl(latitude, longitude)?.let { getResponseFromHttpUrl(it) }
            val jsonResponse = JSONObject(responseFromHttpUrl)
            val status = jsonResponse.getString("status")
            if (status.equals("OK", ignoreCase = true)) {
                val results = jsonResponse.getJSONArray("results")
                val zero = results.getJSONObject(0)
                val addressComponents = zero.getJSONArray("address_components")
                for (i in 0 until addressComponents.length()) {
                    val zero2 = addressComponents.getJSONObject(i)
                    val longName = zero2.getString("long_name")
                    val types = zero2.getJSONArray("types")
                    val type = types.getString(0)
                    if (!TextUtils.isEmpty(longName)) {
                        if (type.equals("street_number", ignoreCase = true)) {
                            address1 = "$longName "
                        } else if (type.equals("route", ignoreCase = true)) {
                            address1 += longName
                        } else if (type.equals("sublocality", ignoreCase = true)) {
                            address2 = longName
                        } else if (type.equals("locality", ignoreCase = true)) {
                            // address2 = address2 + longName + ", ";
                            city = longName
                        } else if (type.equals("administrative_area_level_2", ignoreCase = true)) {
                            county = longName
                        } else if (type.equals("administrative_area_level_1", ignoreCase = true)) {
                            state = longName
                        } else if (type.equals("country", ignoreCase = true)) {
                            country = longName
                        } else if (type.equals("postal_code", ignoreCase = true)) {
                            PIN = longName
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        address1 = ""
        address2 = ""
        city = ""
        state = ""
        country = ""
        county = ""
        PIN = ""
    }
    @Throws(UnsupportedEncodingException::class)
    private fun createUrl(latitude: Double, longitude: Double): String? {
        init()
        return "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=AIzaSyBZeDsuDfoEz3uk6fALVbRzA94fzAbjiWg"
    }
    private fun buildUrl(latitude: Double, longitude: Double): URL? {
        try {
            Log.w("TAG", "buildUrl: " + createUrl(latitude, longitude))
            return URL(createUrl(latitude, longitude))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.e(LOG_TAG, "can't construct location object")
            return null
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val inputStream : InputStream = urlConnection.inputStream
            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")
            if (scanner.hasNext()) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    fun getAddress1(): String? {
        return address1
    }

    fun getAddress2(): String? {
        return address2
    }

    fun getCity(): String? {
        return city
    }

    fun getState(): String? {
        return state
    }

    fun getCountry(): String? {
        return country
    }

    fun getCounty(): String? {
        return county
    }

    fun getPIN(): String? {
        return PIN
    }

}