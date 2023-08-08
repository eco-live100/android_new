package com.app.ecolive.utils



import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.app.ecolive.R
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MyApp : Application() {


    //private static HttpProxyCacheServer proxy;
   // val appID:String="2303501ef777ed28"  // Replace with your App ID
    val appID:String="23292595a3bdb6aa"  // Replace with your App ID
    val region:String="us"  // Replace with your App Region ("eu" or "us")
    override fun onCreate() {
        super.onCreate()

        // init preference keeper
        PreferenceKeeper.setContext(applicationContext)
        application = this
        Companion.ctx = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        val appSettings = AppSettings.AppSettingsBuilder()
            .subscribePresenceForAllUsers()
            .setRegion(region)
            .autoEstablishSocketConnection(true)
            .build()

        CometChat.init(this,appID,appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                    Log.d("TAG", "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG", "Initialization failed with exception: " + p0?.message)
            }

        })



    }


    companion object {

        private lateinit var application: MyApp
        private lateinit var dialog: Dialog
        private var ctx: Context? = null
        var SHARED_PREF_NAME = "Brng_Pref"
        var locationLast:Location? =null
        var lastLocationAddress:String? =null
        fun getAppContext() : Context {
            return ctx!!
        }
        var myApp:MyApp?=null





        open fun showSoftKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun hideSoftKeyboard(activity: Activity) {
            try {
                val inputMethodManager: InputMethodManager = activity
                    .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate(time: Long): String {
            val format = SimpleDateFormat("yyyy/MM/dd")
            var current = format.format(time)
            current = current.replace("/", "-")
            return current

        }

        fun getDateVariation(variation: Int?, currentDate: Date?): Date? {
            val c: Calendar = Calendar.getInstance()
            c.setTime(currentDate)
            c.add(Calendar.DAY_OF_YEAR, +variation!!)
            return c.getTime()
        }

        fun preventDoubleClick(view: View) {
            view.isClickable = false
            view.postDelayed({ view.isClickable = true }, 1000)
        }


        fun dateZoneToTimeFormat(date: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
            val date: Date =
                dateFormat.parse(date) //You will get date object relative to server/client timezone wherever it is parsed
            // dateFormat.parse("2017-04-26T20:55:00.000Z") //You will get date object relative to server/client timezone wherever it is parsed
            //2021-04-20T08:27:38.000000Z
          //  val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm") //If you need time just put specific format for time like 'HH:mm:ss'
            val formatter = SimpleDateFormat("hh:mm aa")

            val dateStr: String = formatter.format(date)
            return dateStr
        }

         fun dateZoneToDateFormat(date: String): String {
             try {
                 val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
                 val date: Date = dateFormat.parse(date) //You will get date object relative to server/client timezone wherever it is parsed
                 val formatter = SimpleDateFormat("dd MMM yyyy hh:mm aa")

                 val dateStr: String = formatter.format(date)
                 return dateStr
             } catch (e: Exception) {
                 return ""
             }
         }

        fun dateZonetoDateFormat2(date: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date: Date =
                dateFormat.parse(date) //You will get date object relative to server/client timezone wherever it is parsed
            // dateFormat.parse("2017-04-26T20:55:00.000Z") //You will get date object relative to server/client timezone wherever it is parsed
            //2021-04-20T08:27:38.000000Z
            val formatter =
                SimpleDateFormat("HH:mm aa") //If you need time just put specific format for time like 'HH:mm:ss'

            val dateStr: String = formatter.format(date)
            return dateStr
        }

        //  2021-04-22 10:11:33

        fun isValidLatLng(lat: Double?, lng: Double?): Boolean {
            lat ?: return false
            lng ?: return false
            if (lat < -90 || lat > 90) {
                return false
            } else if (lng < -180 || lng > 180) {
                return false
            }
            return true
        }


        fun popErrorMsg(titleMsg: String, errorMsg: String, context: Context) {
            // pop error message
            val builder = MaterialAlertDialogBuilder(context, R.style.Theme_MyApp_Dialog_Alert)
           // val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(titleMsg).setMessage(errorMsg)
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }

            val alert = builder.create()
            alert.show()
        }

        fun ShowMassage(ctx: Context, msg: String) {
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle(null).setMessage(msg)
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }

            val alert = builder.create()
            alert.show()
        }

        fun showTost(ctx1: Context, msg1: String) {
            Toast.makeText(ctx1, "" + msg1, Toast.LENGTH_SHORT).show()
        }


        fun getSharedPrefLong(preffConstant: String): Long {
            var longValue: Long = 0
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            longValue = sp.getLong(preffConstant, 0)
            return longValue
        }

        fun setSharedPrefLong(preffConstant: String, longValue: Long) {
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putLong(preffConstant, longValue)
            editor.commit()
        }

        fun getSharedPrefString(preffConstant: String): String {
            var stringValue: String? = ""
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            stringValue = sp.getString(preffConstant, "")
            return stringValue ?: ""
        }

        fun setSharedPrefString(
            preffConstant: String,
            stringValue: String
        ) {
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putString(preffConstant, stringValue)
            editor.commit()
        }

        fun getSharedPrefInteger(preffConstant: String): Int {
            var intValue = 0
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            intValue = sp.getInt(preffConstant, 0)
            return intValue
        }

        fun setSharedPrefInteger(preffConstant: String, value: Int) {
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putInt(preffConstant, value)
            editor.commit()
        }

        fun getSharedPrefFloat(preffConstant: String): Float {
            var floatValue = 0f
            val sp = application.getSharedPreferences(
                preffConstant, 0
            )
            floatValue = sp.getFloat(preffConstant, 0f)
            return floatValue
        }

        fun setSharedPrefFloat(preffConstant: String, floatValue: Float) {
            val sp = application.getSharedPreferences(
                preffConstant, 0
            )
            val editor = sp.edit()
            editor.putFloat(preffConstant, floatValue)
            editor.commit()
        }


        fun getStatus(name: String): Boolean {
            val status: Boolean
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            status = sp.getBoolean(name, false)
            return status
        }

        fun setStatus(name: String, istrue: Boolean) {
            val sp = application.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            // String e = sp.getString(Constants.STATUS, null);
            val editor = sp.edit()
            editor.putBoolean(name, istrue)
            editor.commit()
        }

        fun isValidEmail(target: CharSequence): Boolean {
            return if (target == null) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches()
            }
        }

        @JvmStatic
        fun isConnectingToInternet(context: Context): Boolean {
            var connected = false
            val connectivity = context
                .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.activeNetworkInfo
            connected = info != null && info.isConnected && info.isAvailable
            return connected
        }
        fun getUrlExtention(uri:String):String{
            if (uri.contains(".")) {
                return   uri.substring(uri.lastIndexOf("."))

            }else{
                return ""
            }
        }

        /*    @JvmStatic
        fun showAlrtDialg(context: Context?, msg: String?) {
            spotsDialog = SpotsDialog(context, msg, R.style.Custom)
            spotsDialog!!.setCancelable(false)
            spotsDialog!!.show()
        }

        @JvmStatic
        fun showAlrtDialgWhite(context: Context?, msg: String?) {
            spotsDialog = SpotsDialog(context, msg, R.style.Custom_white)
            spotsDialog!!.setCancelable(false)
            spotsDialog!!.show()
        }

        @JvmStatic
        fun dismisAlrtDialog() {
            try {
                if(spotsDialog==null){
                    Log.d("TAG", "dismisAlrtDialog: null")
                }else{
                    Log.d("TAG", "dismisAlrtDialog: 1")
                }
                spotsDialog!!.dismiss()
            } catch (e: Exception) {
                Log.d("TAG", "dismisAlrtDialog: 2"+e.toString())
            }
        }*/

         /* fun getMarkerBitmapFromView(activity: Activity,resId: Int, strName: String, strAddrs: String): Bitmap? {
            val customMarkerView: View = (activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_marker, null)
            val markerImageView = customMarkerView.findViewById(R.id.profile_image) as ImageView
            val tvTitle = customMarkerView.findViewById(R.id.tvTitle) as TextView
            val tvDesc = customMarkerView.findViewById(R.id.tvDesc) as TextView
            tvTitle.text = strName
            tvDesc.text = strAddrs
            markerImageView.setImageResource(resId)
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
            customMarkerView.buildDrawingCache()
            val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
            val drawable: Drawable? = customMarkerView.background
            if (drawable != null) drawable.draw(canvas)
            customMarkerView.draw(canvas)
            return returnedBitmap
        }*/


        @JvmStatic
        fun createDrawableFromView(activity: Activity, view: View): Bitmap? {
            try {
                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                view.setLayoutParams(
                    RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
                view.layout(0, 0, displayMetrics.widthPixels * 2, displayMetrics.heightPixels * 2)
                view.buildDrawingCache()
                val bitmap = Bitmap.createBitmap(
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                return bitmap

/* if (view.getMeasuredHeight() <= 0) {
view.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
Canvas c = new Canvas(b);
view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
view.draw(c);
return b;
}*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

       /*   fun fetchDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = (Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + (Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta))))
            dist = Math.acos(dist)
              Log.d("ok", "distance1: "+dist)
            dist = rad2deg(dist)
              Log.d("ok", "distance2: "+dist)
            dist = dist * 60 * 1.1515
              Log.d("ok", "distance3: "+dist)
              return Commons.strToDouble(DecimalFormat("##.##").format(dist))

        }*/

        /*fun getDestance(Lat_source: Double, Long_Source: Double, Lat_destination: String, Long_destination: String): Double {
            try {
                var distance: Double
                val latB = Lat_destination.toDouble()
                val lngB = Long_destination.toDouble()
                val locationA = Location("point A")
                locationA.setLatitude(Lat_source)
                locationA.setLongitude(Long_Source)
                val locationB  = Location("point B")
                locationB.setLatitude(latB)
                locationB.setLongitude(lngB)
                distance = locationA.distanceTo(locationB).toDouble()
                println("distance you got is?????$distance")
                distance /= 1000
                return Commons.strToDouble(DecimalFormat("##.##").format(distance))
            } catch (e: NumberFormatException) {
                Log.d("catch MyApplication", "" + e)
                e.printStackTrace()
            }
            return 0.0
        }*/

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }

          fun adjustBoundsForMaxZoomLevel(bounds: LatLngBounds): LatLngBounds? {
              // adjust zoom level (if 1 record found)
            var bounds = bounds
            var sw = bounds.southwest
            var ne = bounds.northeast
            val deltaLat = Math.abs(sw.latitude - ne.latitude)
            val deltaLon = Math.abs(sw.longitude - ne.longitude)
            val zoomN = 0.005 // minimum zoom coefficient
            if (deltaLat < zoomN) {
                sw = LatLng(sw.latitude - (zoomN - deltaLat / 2), sw.longitude)
                ne = LatLng(ne.latitude + (zoomN - deltaLat / 2), ne.longitude)
                bounds = LatLngBounds(sw, ne)
            } else if (deltaLon < zoomN) {
                sw = LatLng(sw.latitude, sw.longitude - (zoomN - deltaLon / 2))
                ne = LatLng(ne.latitude, ne.longitude + (zoomN - deltaLon / 2))
                bounds = LatLngBounds(sw, ne)
            }
            return bounds
        }

    /*          @SuppressLint("SdCardPath")
        fun saveUserDetailChatUsers(hMap: HashMap<String, FSUsersModel>) {
            val path: String
            try {
                path = "/data/data/" + ctx!!.packageName + "/saveUserDetailChatUsers.ser"
                val f = File(path)
                if (f.exists()) {
                    f.delete()
                    println("old file deleted>>>>>>>>> ")
                }
                val fileOut = FileOutputStream(path)
                val out = ObjectOutputStream(fileOut)
                out.writeObject(hMap)
                out.close()
                fileOut.close()
                println("my file replaced>>>>>>>>> ")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        @SuppressLint("SdCardPath")
        fun fetchUserDetailChatUsers():  HashMap<String, FSUsersModel> {
            val path: String
            path = "/data/data/" + ctx!!.getPackageName() + "/saveUserDetailChatUsers.ser"
            val f = File(path)
            var hMap:  HashMap<String, FSUsersModel> =  HashMap<String, FSUsersModel>()
            if (f.exists()) {
                try {
                    System.gc()
                    val fileIn = FileInputStream(path)
                    val innn = ObjectInputStream(fileIn)
                    hMap = innn.readObject() as  HashMap<String, FSUsersModel>
                    innn.close()
                    fileIn.close()
                } catch (e: StreamCorruptedException) {
                    e.printStackTrace()
                } catch (e: OptionalDataException) {
                    e.printStackTrace()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return hMap
        }*/

    }




}
