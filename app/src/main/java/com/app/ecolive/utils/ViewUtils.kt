package com.app.ecolive.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Video
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.ecolive.R
import com.app.ecolive.databinding.AlertDialogBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException
import java.util.*


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun stringToInt(value: String): Int {
    return value.toDouble().toInt()
}

fun Activity.getFcmTokenAndSave() {
    /*FirebaseInstallations.getInstance().getToken(true)
        .addOnSuccessListener(this) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("fire_base_newToken", newToken)
            PreferenceKeeper.instance.fcmTokenSave= newToken
        }*/

    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        Log.e("fire_base_newToken", token)
        PreferenceKeeper.instance.fcmTokenSave= token

    })
}


fun AlertDialog.showDialog(context: Context, message: String) {
    android.app.AlertDialog.Builder(context)
    setCancelable(false)
    val dialogView: View = layoutInflater.inflate(R.layout.progress_dialog, null)
    setView(dialogView)
    create()
    show()
    window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}

fun Context.setAppLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    createConfigurationContext(config)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun AlertDialog.dismissDialog() {
    dismiss()
}

fun String.toRequestBody(): RequestBody {
    return toRequestBody(("application/json; charset=utf-8").toMediaType())
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun SwipeRefreshLayout.isRefresh(): Boolean {
    this.isRefreshing = false
    return false
}


private var alertDialog: AlertDialog? = null
var isShown = false

fun successDialog(
    activity: Activity,
    msg: String?,
    cancel: String,
    ok: String,
    secondButtonClick: View.OnClickListener?
): AlertDialog {
    var secondButtonClick = secondButtonClick
    val builder = AlertDialog.Builder(activity)
    val alertDialogBinding: AlertDialogBinding =
        DataBindingUtil.inflate(activity.layoutInflater, R.layout.alert_dialog, null, false)
    builder.setView(alertDialogBinding.root)
    alertDialogBinding.messageTv.text = msg
    alertDialogBinding.cancelTv.text = cancel
    alertDialogBinding.okTv.text = ok
    alertDialogBinding.cancelTv.setOnClickListener {
        isShown = false
        alertDialog!!.dismiss()
    }

    if (secondButtonClick == null)
        secondButtonClick = View.OnClickListener {
            alertDialog!!.dismiss()
            isShown = false

        }
    alertDialogBinding.okTv.setOnClickListener(secondButtonClick)
    if (!isShown) {
        alertDialog = builder.create()
        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog!!.show()
        isShown = true
    }
    return alertDialog!!
}

fun Context.isDarkTheme(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}


fun handler(progressDialog: AlertDialog, timeDelay: Long) {
    Handler(Looper.getMainLooper()).postDelayed({
        progressDialog.dismiss()
    }, timeDelay)
}

fun handlerFinish(activity: Activity, timeDelay: Long) {
    Handler(Looper.getMainLooper()).postDelayed({
        activity.finish()
    }, timeDelay)
}

fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("ok") {
            snackbar.dismiss()
        }
        val yourSnackBarView = snackbar.view //get your snackbar view
        yourSnackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
            3
    }.show()
}

fun drawableBackColor(view: View, color: Int) {
    val bgShape: GradientDrawable = view.background as GradientDrawable
    bgShape.setColor(color)
}

fun Context.retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
        bitmap = mediaMetadataRetriever.frameAtTime
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}

fun getMimeType(context: Context, uri: Uri): String? {
    val extension: String?

    //Check uri format to avoid null
    extension = if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        //If scheme is a content
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
    } else {
        //If scheme is a File
        //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
    }
    return extension
}

private fun Context.getVideoFrame(uri: Uri, timeInUSeconds: Long): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this, uri)
    val temp = retriever.getFrameAtTime(timeInUSeconds, MediaMetadataRetriever.OPTION_CLOSEST)
    retriever.release()
    return temp
}


fun Context.firstLatterCap(string: String): String? {
    if (string.isNotEmpty()) {
        val fl = string.substring(0, 1).toUpperCase()
        return string.replaceFirst(string.substring(0, 1).toRegex(), fl)
//        return string.substring(0, 1).toUpperCase() + string
    }
    return ""
}

fun Context.callVideo(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(url), "video/mp4")
    startActivity(intent)
}

fun Context.callImage(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(url), "image/*")
    startActivity(intent)
}


@SuppressLint("NewApi")
@Throws(URISyntaxException::class)
fun getFilePath(context: Context, uri: Uri): String? {
    var uri = uri
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    // Uri is different in versions after KITKAT (Android 4.4), we need to
    if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
            context.applicationContext,
            uri
        )
    ) {
        when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            }
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                when (type) {
                    "image" -> {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
    }
    if ("content".equals(uri.scheme, ignoreCase = true)) {
        if (isGooglePhotosUri(uri)) {
            return uri.lastPathSegment
        }
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver
                .query(uri, projection, selection, selectionArgs, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

fun Context.shareIntent(body: String?, share_subject: String?) {
    /*Create an ACTION_SEND Intent*/
    val intent = Intent(Intent.ACTION_SEND)
    /*This will be the actual content you wish you share.*/
    val shareBody = "Here is the share content body"
    /*The type of the content is text, obviously.*/intent.type = "text/plain"
    /*Applying information Subject and Body.*/intent.putExtra(
        Intent.EXTRA_SUBJECT,
        share_subject
    )
    intent.putExtra(Intent.EXTRA_TEXT, shareBody)
    /*Fire!*/
    startActivity(Intent.createChooser(intent, share_subject))
}

fun Context.getRealPathFromURI(contentURI: Uri): String? {
    val result: String?
    val cursor: Cursor = contentResolver.query(contentURI, null, null, null, null)!!
    if (cursor == null) { // Source is Dropbox or other similar local file path
        result = contentURI.path
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return result
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}

fun getRealVideoPathFromURI(contentResolver: ContentResolver, contentURI: Uri): String? {
    val cursor = contentResolver.query(
        contentURI, null, null, null,
        null
    )
    return if (cursor == null) contentURI.path else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(Video.VideoColumns.DATA)
        try {
            cursor.getString(idx)
        } catch (exception: java.lang.Exception) {
            null
        }
    }
}

fun Context.bitMapImage(imageUri: Uri): Bitmap {
    var decodedString: ByteArray? = null
    try {
        val bitmaps = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        val stream = ByteArrayOutputStream()
        bitmaps.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val byteArray: ByteArray = stream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
        decodedString = Base64.decode(encoded, Base64.DEFAULT)

    } catch (e: IOException) {
        e.printStackTrace()
    }

    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString!!.size)
}

fun Context.videoThumbnail(filePath: String, file: File): File {
    val thumbnailFile = File(cacheDir, "${System.currentTimeMillis()}_thumb.jpg")
    Log.d("file_path_before", thumbnailFile.toString())
    var bitmap: Bitmap? = null
    if (Build.VERSION.SDK_INT <= 29) {
        bitmap = ThumbnailUtils.createVideoThumbnail(
            filePath,
            MediaStore.Images.Thumbnails.MINI_KIND
        )

    } else {
        // TODO: 4/17/2020 here we will do code for crete thumnail for latest api version 29 bcoz createVideoThumbnail is depricate for this version
        val signal = CancellationSignal()
        val size = Size(100, 100)
//                    val file: File = File(filePath)
        bitmap = ThumbnailUtils.createVideoThumbnail(
            file,
            size,
            signal
        )
    }

    val bos = ByteArrayOutputStream()
    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
    val bitmapData = bos.toByteArray()

//                val fos: FileOutputStream = FileOutputStream(thumbnailFile)
//                fos.write(bitmapData)
//                fos.flush()
//                fos.close()

    try {
        if (!thumbnailFile.exists()) {
            thumbnailFile.createNewFile()
        }
        val fos = FileOutputStream(thumbnailFile)
        fos.write(bitmapData)
        fos.close()
    } catch (e: java.lang.Exception) {
        Log.e("TAG", e.message!!)
    }

    return thumbnailFile
}


fun Context.imageThumbnail(filePath: String, file: File): File {
    val thumbnailFile = File(cacheDir, "${System.currentTimeMillis()}_thumb.jpg")
    Log.d("file_path_before", thumbnailFile.toString())
    var bitmap: Bitmap? = null
    if (Build.VERSION.SDK_INT <= 29) {
//        bitmap = ThumbnailUtils.createImageThumbnail(
//            filePath,
//            MediaStore.Images.Thumbnails.MINI_KIND
//        )

        val THUMBSIZE = 128
        bitmap = ThumbnailUtils.extractThumbnail(
            BitmapFactory.decodeFile(file.absolutePath),
            THUMBSIZE,
            THUMBSIZE
        )

    } else {
        val signal = CancellationSignal()
        val size = Size(100, 100)
        bitmap = ThumbnailUtils.createImageThumbnail(
            file,
            size,
            signal
        )
    }

    val bos = ByteArrayOutputStream()
    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
    val bitmapData = bos.toByteArray()

//                val fos: FileOutputStream = FileOutputStream(thumbnailFile)
//                fos.write(bitmapData)
//                fos.flush()
//                fos.close()

    try {
        if (!thumbnailFile.exists()) {
            thumbnailFile.createNewFile()
        }
        val fos = FileOutputStream(thumbnailFile)
        fos.write(bitmapData)
        fos.close()
    } catch (e: java.lang.Exception) {
        Log.e("TAG", e.message!!)
    }

    return thumbnailFile
}