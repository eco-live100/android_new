package com.app.ecolive.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.Settings.Secure
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.app.ecolive.R
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Utils {

    companion object {
        private var fragmentManager: FragmentManager? = null
        fun getConvertedFile(folder: String, fileName: String): File {
            val f = File(folder)

            if (!f.exists())
                f.mkdirs()

            return File(f.path + File.separator + fileName)
        }

        fun refreshGallery(path: String, context: Context) {

            val file = File(path)
            try {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(file)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun isValidPassword(password: String?): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val PASSWORD_PATTERN =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(password)
            return matcher.matches()
        }

        fun hideSoftKeyBoard(context: Context, view: View) {
            try {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }

        }

        fun getDrawableByName(context: Context, resourceName: String?): Drawable {
            val resources: Resources = context.resources
            val resourceId: Int = resources.getIdentifier(
                resourceName, "drawable",
                context.packageName
            )
            return resources.getDrawable(resourceId)
        }

        fun getDate(date: String): String {
            var str: String = ""
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
                val sdf2 = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
                str = sdf2.format(sdf.parse(date))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        fun getTime(date: String): String {
            var str: String = ""
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
                val sdf2 = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
                str = sdf2.format(sdf.parse(date))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        /*
                fun showSnackBarFromTop(view: View, message: String, context: Context) {
                    val snackBarView = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                  //  snackBarView.changeFont()
                    val snackView = snackBarView.view
                    val params = snackView.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    snackView.layoutParams = params
                    snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_20213A))
                    snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackBarView.show()
                }
        */

        fun Int.dpToPx(context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun marginLayout(margin: Int, view: View) {
            val params: CollapsingToolbarLayout.LayoutParams =
                CollapsingToolbarLayout.LayoutParams(
                    Toolbar.LayoutParams.MATCH_PARENT,
                    Toolbar.LayoutParams.WRAP_CONTENT
                )
            params.setMargins(margin, margin, margin, margin)
            view.layoutParams = params
        }

        fun googleMapRedirect(context: Context, latitude: String, longitude: String) {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            }
        }

        fun listIsEmpty(
            errorIv: ConstraintLayout,
            scrollViews: View,
            titleTv: TextView,
            messageTv: TextView,
            title: String?,
            message: String?
        ) {
            errorIv.visibility = View.VISIBLE
            scrollViews.visibility = View.GONE
            titleTv.text = title
            messageTv.text = message

        }

        fun listIsNotEmpty(errorIv: ConstraintLayout, scrollViews: View) {
            errorIv.visibility = View.GONE
            scrollViews.visibility = View.VISIBLE
        }

        fun getScreenWidth(context: Context): Int {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun milliSecondsToTimer(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours.toString() + ":"
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds
            } else {
                secondsString = "" + seconds
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString

            // return timer string
            return finalTimerString
        }

        private var retryCustomAlert: AlertDialog? = null
        var isShown = false
        var progressDialog: AlertDialog? = null

        fun statusBarHideShow(activity: Activity) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        fun getColoredString(mString: String?, colorId: Int): Spannable? {
            val spannable: Spannable = SpannableString(mString)
            spannable.setSpan(
                ForegroundColorSpan(colorId),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                UnderlineSpan(),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            Log.d(ContentValues.TAG, spannable.toString())
            return spannable
        }

        fun getDeviceId(contentResolver: ContentResolver): String {
            return Secure.getString(contentResolver, Secure.ANDROID_ID)
        }


        fun changeStatusColor(activity: Activity, resourceColor: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor =
                    ContextCompat.getColor(activity.applicationContext, resourceColor)
            }
        }

        fun changeStatusTextColor(activity: Activity) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        fun changeStatusTextColor2(activity: Activity) {
            val window: Window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setStatusBarColor(activity.getResources().getColor(R.color.white))

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.color_050D4C));
        }

        fun dpToPx(context: Context, dp: Int): Int {
            val displayMetrics = context.resources.displayMetrics
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
        }

        fun pxToDp(context: Context, px: Int): Int {
            val displayMetrics = context.resources.displayMetrics
            return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
        }

        fun ratioOfScreen(context: Activity, ratio: Float): Int {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            return (pxToDp(context, width) * ratio).toInt()
        }


        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }

        fun shareIntent(context: Context, subject: String?, shareContent: String?) {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val shareBody = shareContent
            /*The type of the content is text, obviously.*/intent.type = "text/plain"
            /*Applying information Subject and Body.*/
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            /*Fire!*/context.startActivity(Intent.createChooser(intent, shareBody))
        }

        //    public static String getDirectionsUrl(LatLng origin, LatLng dest) {
        //
        //        // Origin of route
        //        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        //
        //        // Destination of route
        //        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //
        //        // Sensor enabled
        //        String sensor = "sensor=false";
        //        String mode = "mode=driving";
        //        String alternative = "alternatives=true";
        //        String key = "key="+"AIzaSyBT20hQ7W44fZzKO0TwKppeqVZwUzaPBfI";
        //        // Building the parameters to the web service
        //        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + alternative+ "&" + key;
        //
        //        // Output format
        //        String output = "json";
        //
        //        // Building the url to the web service
        //        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        //
        //        Log.e("url", url);
        //
        //        return url;
        //    }
        fun getCarBitmap(context: Context, car: Int): Bitmap? {
            val bitmap = BitmapFactory.decodeResource(context.resources, car)
            return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
        }

        fun strikeText(textView: TextView) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        fun Toast(context: Context?, string: String?) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }

        fun getMimeType(url: String?): String? {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }

        fun showMessage(ctx: Context?, msg: String?) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
        }


        fun hideSoftKeyBoard(activity: Activity) {
            try {
                val inputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                //check if no view has focus.
                val v = activity.currentFocus ?: return
                inputMethodManager.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun tintColor(context: Context?, imageView: ImageView, color: Int) {
            if (color == 0) {
                imageView.clearColorFilter()
            } else {
                imageView.setColorFilter(ContextCompat.getColor(context!!, color))
            }
        }


        fun isNotEmptyNull(string: String): Boolean {
            return !TextUtils.isEmpty(string) && string != "null"
        }

        fun isEmptyNull(string: String): Boolean {
            return TextUtils.isEmpty(string) || string == "null"
        }

//        fun getAddress(latitude: Double, longitude: Double): String {
//            try {
//                val geo = Geocoder(
//                    getApplicationContext(),
//                    Locale.getDefault()
//                )
//                val addresses: List<Address> = geo.getFromLocation(latitude, longitude, 1)
//                if (addresses.isEmpty()) {
//                    Log.d("Waiting for Location", "")
//                } else {
//                    if (addresses.isNotEmpty()) {
//
//                        return addresses[0].featureName
//                            .toString() + ", " + addresses[0].locality + ", " + addresses[0].adminArea + ", " + addresses[0].countryName
//
//                        //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
//                    }
//                }
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace() // getFromLocation() may sometimes fail
//            }
//            return ""
//        }

        fun currentDate(
            outPutFormat: String?,
            calendarView: CalendarView?,
            setMin: Boolean,
            setMax: Boolean
        ): String? {
            val calendar = Calendar.getInstance()
            val date = calendar.time
            calendar[Calendar.DATE] = calendar.getActualMaximum(Calendar.DATE)
            if (setMax) {
                val endOfMonth = calendar.timeInMillis
                calendarView!!.maxDate = endOfMonth
            }
            if (setMin) {
                val startOfMonth = calendar.timeInMillis
                calendarView!!.minDate = startOfMonth
            }
            @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat(outPutFormat)
            return df.format(date)
        }

        fun currentTime(outPutFormat: String?): String? {
            val calendar = Calendar.getInstance()
            val date = calendar.time
            @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat(outPutFormat)
            return df.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        @Throws(ParseException::class)
        fun formatDateFromDateString(
            inputDateFormat: String?,
            outputDateFormat: String?,
            inputDate: String?
        ): String? {
            val inputFormat = SimpleDateFormat(inputDateFormat, Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat(outputDateFormat, Locale.ENGLISH)
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        @Throws(ParseException::class)
        fun formatDateFromDateDate(
            inputDateFormat: String?,
            outputDateFormat: String?,
            inputDate: String?
        ): Date? {
            val inputFormat = SimpleDateFormat(inputDateFormat, Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat(outputDateFormat, Locale.US)
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")
            return inputFormat.parse(inputDate!!)
        }

        fun printLog(toString: String?) {
            Log.d("sdfasdfasdfas", toString!!)
        }

        fun checkPassword(password: String?): Boolean? {
            val re = arrayOf(
                "[a-zA-Z]",
                //"[?=.*[@#$%!\\-_?&])(?=\\\\S+$]",
                "[0-9]"
            )
            for (r in re) {
                if (!Pattern.compile(r).matcher(password!!).find()) return false
            }
            return true
        }

        fun isValidEmail(email: String?): Boolean {
            val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            val pattern = Pattern.compile(EMAIL_PATTERN)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun statusBarColor(context: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = context.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = context.resources.getColor(R.color.purple_200)
            }
        }

        fun callMethod(context: Context, number: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            // intent.putExtra("to")
            (context as Activity).startActivity(intent)
        }

        fun emailSend(context: Context, recipient: String, subject: String, message: String) {
            /*ACTION_SEND action to launch an email client installed on your Android device.*/
            val mIntent = Intent(Intent.ACTION_SEND)
            /*To send an email you need to specify mailto: as URI using setData() method
            and data type will be to text/plain using setType() method*/
            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"
            // put recipient email in intent
            /* recipient is put as array because you may wanna send email to multiple emails
               so enter comma(,) separated emails, it will be stored in array*/
            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            //put the Subject in the intent
            mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            //put the message in the intent
            mIntent.putExtra(Intent.EXTRA_TEXT, message)


            try {
                //start email intent
                (context as Activity).startActivity(
                    Intent.createChooser(
                        mIntent,
                        "Choose Email Client..."
                    )
                )
            } catch (e: Exception) {
                //if any thing goes wrong for example no email client application or any exception
                //get and show exception message
                Toast(context, e.message.toString())
            }

        }


//
//        open fun alertDialog(context: Context, title: String?, msg: String?) {
//            val dialogBuilder = AlertDialog.Builder(context)
//            dialogBuilder.setCancelable(false)
//            val inflater = (context as Activity).layoutInflater
//            val dialogView: View = inflater.inflate(R.layout.retry_alert, null)
//            dialogBuilder.setView(dialogView)
//            val txtRAMsg = dialogView.findViewById<View>(R.id.txtRAMsg) as TextView
//            val txtRAFirst = dialogView.findViewById<View>(R.id.txtRAFirst) as TextView
//            val txtRASecond = dialogView.findViewById<View>(R.id.txtRASecond) as TextView
//            val deviderView = dialogView.findViewById(R.id.deviderView) as View
//            val dialog = dialogBuilder.create()
//            txtRAMsg.text = msg
//            txtRAFirst.text = "Ok"
//            txtRASecond.visibility = View.GONE
//            txtRAFirst.setOnClickListener { v: View? -> dialog.dismiss() }
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.show()
//        }

//    public static void setImage(Context context, ImageView imageView, String url) {
//        Glide.with(context).load(url).centerCrop().placeholder(R.drawable.demo_car).into(imageView);
//    }
//
//
//    public static Animation zoomAnimation(Context context){
//        Animation animZoomIn = AnimationUtils.loadAnimation(context,R.anim.zoom_in);
//        return animZoomIn;
//    }


        fun sendSMS(context: Context, number: String?, message: String?) {
            Log.i("Send SMS", "")
            val smsIntent = Intent(Intent.ACTION_VIEW)
            smsIntent.data = Uri.parse("smsto:")
            smsIntent.type = "vnd.android-dir/mms-sms"
            smsIntent.putExtra("address", number + "")
            smsIntent.putExtra("sms_body", message)
            try {
                context.startActivity(smsIntent)
                Log.i("Finished sending SMS...", "")
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun getSelectedFile(context: Context, imageView: ImageView): File? {
            return try {
                val username = "Dropless"

                //            get bitmap from image set on  imageview and convert to byte array
                val bitmapDrawable = imageView.drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
                val bitmapdata = byteArrayOutputStream.toByteArray()
                byteArrayOutputStream.flush()
                byteArrayOutputStream.close()
                val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss")
                val timeStamp = dateFormat.format(Date())
                val file = File(context.filesDir, username + "_profile_" + timeStamp + ".png")

                //            insert byte array into file output stream with name
                val fileOutputStream = context.openFileOutput(
                    username + "_profile_" + timeStamp + ".png",
                    Context.MODE_PRIVATE
                )
                fileOutputStream.write(bitmapdata)
                fileOutputStream.flush()
                fileOutputStream.close()
                val profileImageFile = File(file.absolutePath)
                Log.d(ContentValues.TAG, "file retrieve$profileImageFile")
                val fileUri = Uri.fromFile(profileImageFile)
                Log.d(ContentValues.TAG, "file Uri$fileUri")
                profileImageFile
            } catch (fnfe: FileNotFoundException) {
                fnfe.printStackTrace()
                null
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                null
            }
        }


        fun smsMethod(context: Context, number: String?, message: String?) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_MESSAGING)
            //        intent.setData(Uri.parse("smsto:"+number)); // This ensures only SMS apps respond
//        intent.putExtra("sms_body", message);
//        if (intent.resolveActivity(context.getPackageManager()) != null) {
            (context as Activity).startActivity(intent)
//        }
        }

        fun exitFullscreen(activity: Activity) {
            if (Build.VERSION.SDK_INT > 10) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.window.statusBarColor =
                        activity.resources.getColor(R.color.purple_200)
                }
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            } else {
                activity.window
                    .setFlags(
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                    )
            }
        }

        fun makeTextViewResizable(
            tv: TextView,
            maxLine: Int, expandText: String, viewMore: Boolean
        ) {
            if (tv.tag == null) {
                tv.tag = tv.text
            }
            val vto = tv.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val obs = tv.viewTreeObserver
                    obs.removeGlobalOnLayoutListener(this)
                    if (maxLine == 0) {
                        val lineEndIndex = tv.layout.getLineEnd(0)
                        val text = tv.text.subSequence(
                            0,
                            lineEndIndex - expandText.length + 1
                        )
                            .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                tv.text
                                    .toString(), tv, maxLine, expandText,
                                viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                        val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                        val text = tv.text.subSequence(
                            0,
                            lineEndIndex - expandText.length + 1
                        )
                            .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                tv.text
                                    .toString(), tv, maxLine, expandText,
                                viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else {
                        val lineEndIndex = tv.layout.getLineEnd(
                            tv.layout.lineCount - 1
                        )
                        val text = tv.text.subSequence(0, lineEndIndex)
                            .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                tv.text
                                    .toString(), tv, lineEndIndex, expandText,
                                viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                }
            })
        }

        fun addClickablePartTextViewResizable(
            strSpanned: String, tv: TextView, maxLine: Int,
            spanableText: String, viewMore: Boolean
        ): SpannableStringBuilder? {
            val ssb = SpannableStringBuilder(strSpanned)
            if (strSpanned.contains(spanableText)) {
                ssb.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            if (viewMore) {
                                tv.layoutParams = tv.layoutParams
                                tv.setText(
                                    tv.tag.toString(),
                                    TextView.BufferType.SPANNABLE
                                )
                                tv.invalidate()
                                makeTextViewResizable(
                                    tv, -5, "...Read Less",
                                    false
                                )
                                tv.setTextColor(Color.BLACK)
                            } else {
                                tv.layoutParams = tv.layoutParams
                                tv.setText(
                                    tv.tag.toString(),
                                    TextView.BufferType.SPANNABLE
                                )
                                tv.invalidate()
                                makeTextViewResizable(
                                    tv, 5, "...Read More",
                                    true
                                )
                                tv.setTextColor(Color.BLACK)
                            }
                        }
                    }, strSpanned.indexOf(spanableText),
                    strSpanned.indexOf(spanableText) + spanableText.length, 0
                )
            }
            return ssb
        }

        @Throws(Throwable::class)
        fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
            var bitmap: Bitmap? = null
            var mediaMetadataRetriever: MediaMetadataRetriever? = null
            try {
                mediaMetadataRetriever = MediaMetadataRetriever()
                if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
                    videoPath,
                    HashMap<String, String>()
                ) else mediaMetadataRetriever.setDataSource(videoPath)
                bitmap = mediaMetadataRetriever.getFrameAtTime(
                    1,
                    MediaMetadataRetriever.OPTION_CLOSEST
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                throw Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.message
                )
            } finally {
                mediaMetadataRetriever?.release()
            }
            return bitmap
        }

        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS

        fun getTimeAgo(time: Long): String? {
            var time = time
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now: Long = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }

            val diff = now - time
            return if (diff < MINUTE_MILLIS) {
                "just now"
            } else if (diff < 2 * MINUTE_MILLIS) {
                "a minute ago"
            } else if (diff < 50 * MINUTE_MILLIS) {
                diff.div(MINUTE_MILLIS).toString() + " minutes ago"
            } else if (diff < 90 * MINUTE_MILLIS) {
                "an hour ago"
            } else if (diff < 24 * HOUR_MILLIS) {
                diff.div(HOUR_MILLIS).toString() + " hours ago"
            } else if (diff < 48 * HOUR_MILLIS) {
                "yesterday"
            } else {
                diff.div(DAY_MILLIS).toString() + " days ago"
            }
        }


        fun priceMultiplyByQty(price: String, qty: String): String {
            var finalAmount = ""
            val intPrice = price.toInt()
            val intQty = qty.toInt()
            val intAmount = intPrice * intQty
            finalAmount = intAmount.toString()
            return finalAmount
        }


        fun checkingPermissionIsEnabledOrNot(mContext: Context): Boolean {
            val cameraPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO)
            else
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                    //   Manifest.permission.ACCESS_FINE_LOCATION
                )

           // ContextCompat.checkSelfPermission( activity: Activity,cameraPermission) == PackageManager.PERMISSION_GRANTED
            // val locationPermission = mContext.packageManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,mContext.packageName)
            // return cameraPermission == PackageManager.PERMISSION_GRANTED  && locationPermission == PackageManager.PERMISSION_GRANTED
            //return ContextCompat.per( mContext,cameraPermission) == PackageManager.PERMISSION_GRANTED

            var totalCp=0
            for (cp in cameraPermission){
                if (ContextCompat.checkSelfPermission( mContext,cp) == PackageManager.PERMISSION_GRANTED){
                    totalCp++
                }
            }
            if (totalCp>=2){
                return true
            }
            return false

        }

        fun requestMultiplePermission(activity: Activity, requestPermissionCode: Int) {
            ActivityCompat.requestPermissions(
                activity,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    arrayOf(
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA)
                else
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ),
                requestPermissionCode
            )
        }

        fun saveImage(mContext: Context, myBitmap: Bitmap): String {
            val bytes = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
            val wallpaperDirectory = mContext.getDir("images", Context.MODE_PRIVATE)

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs()
            }
            try {
                val f = File(
                    wallpaperDirectory, ((Calendar.getInstance()
                        .timeInMillis).toString() + ".jpg")
                )
                f.createNewFile()
                val fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                MediaScannerConnection.scanFile(
                    mContext,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null
                )
                fo.close()
                println("f_absolutePath $f.absolutePath")
                return f.absolutePath
            } catch (e1: IOException) {
                e1.printStackTrace()
                println("error: ${e1.message}")
            }
            return ""
        }

        fun setImageFullPath(context: Context?, imageView: ImageView?, url: String?) {
            try {
                imageView?.let {
                    Glide.with(context!!).load(url)
                        .placeholder(R.drawable.app_logo_bw)
                        .error((R.drawable.app_logo_bw))//app_logo_bgtrans
                        .into(it)
                }
            } catch (e: Exception) {
                Log.d("Glide", "setImageFullPath: " + e)
            }
        }

    }


}