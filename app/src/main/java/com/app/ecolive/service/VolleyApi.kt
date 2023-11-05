

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.ecolive.R
import com.app.ecolive.service.NetworkHelper
import com.app.ecolive.service.VolleyApiCompleteTask
import com.app.ecolive.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import java.util.*
/**
 * Created by mukesh verma on 3/12/17.
 */
class VolleyApi {
    var url: String
    var params: HashMap<String, String>? = null
    var webCompleteTask: VolleyApiCompleteTask
    var taskcode: Int
    var context: Context
    var contextnet: Context? = null
    var `object`: JSONObject? = null
    var isProgress = true
    var mathod = 0
    var progressDialog: ProgressDialog? = null

    constructor(
        context: Context,
        url: String,
        params: HashMap<String, String>?,
        webCompleteTask: VolleyApiCompleteTask,
        taskcode: Int,
        mathod: Int
    ) {
        this.url = url
        this.params = params
        this.webCompleteTask = webCompleteTask
        this.taskcode = taskcode
        this.context = context
        this.mathod = mathod
        progressDialog = ProgressDialog(context)
//        Network.activityname = "webview"
        //        session = new_image SessionManagement(context);
        //registerReceiver(myReceiver, new_image IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


//        if (mathod==1){
//            volleyStringRequest();
//        }
//        else  if (mathod==2){
//            volleyStringRequestLogOut();
//        }
//        else {
//            volleyStringRequestGet();
//        }
        try {
            if (!NetworkHelper(context).isNetworkConnected()) {
                Utils.showMessage(context, context.getString(R.string.network_error_msg))
                return
            } else {
                Utils.hideSoftKeyBoard(context as Activity)
                when (mathod) {
                    1 -> {
                        volleyStringRequest()
                    }
                    2 -> {
                        volleyStringRequestLogOut()
                    }
                    3 -> {
                        volleyStringRequestGetWithoutProgressBar()
                    }
                    0 -> {
                        volleyStringRequestGet()
                    }
                    5 -> {
                        volleyStringRequestwithoutProgressBar()
                    }
                    else -> {
                        volleyStringRequestGetLn()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    constructor(
//        context: Context,
//        url: String,
//        params: HashMap<String, String>?,
//        webCompleteTask: VolleyApiCompleteTask,
//        taskcode: Int,
//        isProgress: Boolean
//    ) {
//        this.url = url
//        this.params = params
//        this.webCompleteTask = webCompleteTask
//        this.taskcode = taskcode
//        this.context = context
//        this.isProgress = isProgress
//        progressDialog = ProgressDialog(context)
//        volleyStringRequest()
//
//        /* if (!MyApplication.getInstance().isConnectingToInternet(context)) {
//            MyApplication.showMessage(context, context.getString(R.string.internet_issue));
//            return;
//        } else {
//            MyApplication.getInstance().hideSoftKeyBoard((Activity) context);
//
//            volleyStringRequest();
//
//        }*/
//    }
//
//    constructor(
//        context: Context,
//        url: String,
//        `object`: JSONObject?,
//        webCompleteTask: VolleyApiCompleteTask,
//        taskcode: Int,
//        mathod: Int
//    ) {
//        this.url = url
//        this.`object` = `object`
//        this.webCompleteTask = webCompleteTask
//        this.taskcode = taskcode
//        this.context = context
//        progressDialog = ProgressDialog(context)
//        try {
//            if (!Network.isConnectingToInternet(context)) {
//                Utils.showMessage(context, context.getString(R.string.network_error_msg))
//                return
//            } else {
//                Utils.hideSoftKeyBoard(context as Activity)
//                if (mathod == 1) {
//                    volleyStringRequestObject()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun volleyStringRequestwithoutProgressBar() {
//        if (isProgress) {
//            progressDialog = ProgressDialog.show(context, "",  context.getString(R.string.api_hiting));
//            progressDialog.setCancelable(true);
//        }
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response -> //                if (progressDialog != null)
                //                    progressDialog.dismiss();
                webCompleteTask.onComplete(response, taskcode)
            },
            Response.ErrorListener { error ->
                //                if (progressDialog != null)
                //                    progressDialog.dismiss();
                try {
                    val networkResponse = error.networkResponse
                    var errorMessage: String? = "Unknown error"
                    if (networkResponse == null) {
                        if (error.javaClass == TimeoutError::class.java) {
                            errorMessage = "Request timeout"
                        } else if (error.javaClass == NoConnectionError::class.java) {
                            errorMessage = "Failed to connect server"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        val result = String(networkResponse.data)
                        Log.d("error response", result)
                        try {
                            val response = JSONObject(result)
                            val errorObje = response.getJSONObject("error")
                            val status = errorObje.getString("statusCode")
                            val message = errorObje.getString("message")
                            errorMessage = message

                            //                            JSONObject response = new_image JSONObject(result);
                            //                            JSONObject jobj=new_image JSONObject(response.getString("error"));
                            //
                            //                            String message = jobj.getString("message");
                            //                            String status= jobj.getString("statusCode");
                            if (status.compareTo("401") == 0) {
                                if (message == "Incorrect password") {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                } else if (message == "Account does not exist") {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                } else {
                                    //                                    Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                                    //                                    Intent i = new_image Intent(context, Login.class);
                                    //                                    session.logoutUser();
                                    //                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    //                                    context.startActivity(i);
                                    //                                    ((Activity)context).finish();
                                }
                            } else {
                                Log.e("Error Message", message)
                                //                                Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found"
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = "$message 401 Please login again"
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = "$message Something is getting wrong"
                                }
                            }
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    //                    Log.i("Error response", new_image String(error.networkResponse.data));
                    error.printStackTrace()
                } finally {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header_param = HashMap<String, String>()
//                if (Prefs.getBoolean(Constans.IsLogin, false)) header_param["Authorization"] =
//                    "Bearer " + Prefs.getString(Constans.Authorization, "")
                //                header_param.put("ln", Utils.getLangId(context, RequestCode.LangId));
                return header_param
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }

//    fun volleyStringRequestObject() {
//        if (isProgress) {
//            progressDialog =
//                ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
//            progressDialog.setCancelable(true)
//        }
//        /*cvfdbvfdb*/
//        val request: JsonObjectRequest = object : JsonObjectRequest(
//            Method.POST, url, `object`,
//            Response.Listener { response ->
//                if (progressDialog != null) progressDialog!!.dismiss()
//                webCompleteTask.onComplete(response.toString() + "", taskcode)
//                //parseDirectionsData(response);
//            },
//            Response.ErrorListener { error ->
//                error.printStackTrace()
//                if (progressDialog != null) progressDialog!!.dismiss()
//                try {
//                    val networkResponse = error.networkResponse
//                    var errorMessage = "Unknown error"
//                    if (networkResponse == null) {
//                        if (error.javaClass == TimeoutError::class.java) {
//                            errorMessage = "Request timeout"
//                        } else if (error.javaClass == NoConnectionError::class.java) {
//                            errorMessage = "Failed to connect server"
//                        }
//                    } else {
//                        val result = String(networkResponse.data)
//                        Log.d("error response", result)
//
//                        //                        try {
//                        //                            JSONObject response = new_image JSONObject(result);
//                        //                            JSONObject errorObje = response.getJSONObject("error");
//                        //                            String status = errorObje.getString("statusCode");
//                        //                            String message = errorObje.getString("message");
//                        //                            errorMessage = message;
//                        //
//                        //                            if (status.compareTo("401") == 0) {
//                        ////                                if (message.equals("Incorrect password")) {
//                        ////
//                        ////                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        ////                                } else if (message.equals("Account does not exist")) {
//                        ////                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        ////                                } else {
//                        ////                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        ////                                    Intent i = new_image Intent(context, Login.class);
//                        ////                                    session.logoutUser();
//                        ////                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//                        ////                                    context.startActivity(i);
//                        ////                                    ((Activity)context).finish();
//                        ////                                }
//                        //                            } else {
//                        //                                Log.e("Error Message", message);
//                        ////                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        //                                if (networkResponse.statusCode == 404) {
//                        //                                    errorMessage = "Resource not found";
//                        //                                } else if (networkResponse.statusCode == 401) {
//                        //                                    errorMessage = message + " 401 Please login again";
//                        //                                } else if (networkResponse.statusCode == 400) {
//                        //                                    errorMessage = message;
//                        //                                } else if (networkResponse.statusCode == 500) {
//                        //                                    errorMessage = message + " Something is getting wrong";
//                        //                                }
//                        //                            }
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
//
//                        //                        } catch (JSONException e) {
//                        //                            e.printStackTrace();
//                        //                        }
//                    }
//                    //                    Log.i("Error response", new_image String(error.networkResponse.data));
//                    error.printStackTrace()
//                } finally {
//                }
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                return params
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                //                header_param.put("Authorization", Utils.getInstance(context).getAccessToken());
////                header_param.put("ln", Utils.getLangId(context, Constrants.UserLang));
//                return HashMap()
//            }
//        }
//        request.retryPolicy = DefaultRetryPolicy(
//            60000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//        Volley.newRequestQueue(context).add(request)
//    }
//
//    fun volleyJsonRequest() {
//        val progressDialog =
//            ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
//        val jsObjRequest = JsonObjectRequest(
//            Request.Method.POST, url, `object`,
//            { response ->
//                try {
//                    progressDialog.dismiss()
//                    webCompleteTask.onComplete(response.toString(), taskcode)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        ) { progressDialog.dismiss() }
//        Volley.newRequestQueue(context).add(jsObjRequest)
//    }

    fun volleyStringRequest() {
        if (isProgress) {
            progressDialog =
                ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
            progressDialog!!.setCancelable(false)
        }
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (progressDialog != null) progressDialog!!.dismiss()
                webCompleteTask.onComplete(response, taskcode)
            },
            Response.ErrorListener {
                if (progressDialog != null) progressDialog!!.dismiss()

                //                try {
                //                    NetworkResponse networkResponse = error.networkResponse;
                //                    String errorMessage = "Unknown error";
                //                    if (networkResponse == null) {
                //                        if (error.getClass().equals(TimeoutError.class)) {
                //                            errorMessage = "Request timeout";
                //                        } else if (error.getClass().equals(NoConnectionError.class)) {
                //                            errorMessage = "Failed to connect server";
                //                        }
                //                        Toast.makeText(context, "" + errorMessage, Toast.LENGTH_LONG).show();
                //                        Log.e("failedTTOOO", String.valueOf(error));
                //                    } else {
                //                        String result = new_image String(networkResponse.data);
                //                        Log.d("error response", result);
                //                        try {
                //                            JSONObject response = new_image JSONObject(result);
                //                            JSONObject errorObje = response.getJSONObject("error");
                //                            String status = errorObje.getString("statusCode");
                //                            String message = errorObje.getString("message");
                //                            errorMessage = message;
                //
                ////                            JSONObject response = new_image JSONObject(result);
                ////                            JSONObject jobj=new_image JSONObject(response.getString("error"));
                ////
                ////                            String message = jobj.getString("message");
                ////                            String status= jobj.getString("statusCode");
                //                            if (status.compareTo("401") == 0) {
                //                                if (message.equals("Incorrect password")) {
                //                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                //                                } else if (message.equals("Account does not exist")) {
                //                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                //                                } else {
                ////                                    Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                ////                                    Intent i = new_image Intent(context, Login.class);
                ////                                    session.logoutUser();
                ////                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                ////                                    context.startActivity(i);
                ////                                    ((Activity)context).finish();
                //                                }
                //                            } else {
                //                                Log.e("Error Message", message);
                ////                                Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                //                                if (networkResponse.statusCode == 404) {
                //                                    errorMessage = "Resource not found";
                //                                } else if (networkResponse.statusCode == 401) {
                //                                    errorMessage = message + " 401 Please login again";
                //                                } else if (networkResponse.statusCode == 400) {
                //                                    errorMessage = message + " Check your inputs";
                //                                } else if (networkResponse.statusCode == 500) {
                //                                    errorMessage = message + " Something is getting wrong";
                //                                }
                //                            }
                //                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                //                        } catch (JSONException e) {
                //                            e.printStackTrace();
                //                        }
                //                    }
                ////                    Log.i("Error response", new_image String(error.networkResponse.data));
                //                    error.printStackTrace();
                //                } finally {
                //
                //                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header_param = HashMap<String, String>()
//                if (Prefs.getBoolean(Constans.IsLogin, false)) header_param["Authorization"] =
//                    "Bearer " + Prefs.getString(Constans.Authorization, "")
                //                header_param.put("Authorization", Utils.getInstance(context).getAccessToken());
//                header_param.put("ln", Utils.getLangId(context, RequestCode.LangId));
                return header_param
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }

//    fun volleyStringRequestDelete() {
//        if (isProgress) {
//            progressDialog =
//                ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
//            progressDialog.setCancelable(true)
//        }
//        val stringRequest: StringRequest = object : StringRequest(
//            Method.DELETE, url,
//            Response.Listener { response ->
//                if (progressDialog != null) progressDialog!!.dismiss()
//                webCompleteTask.onComplete(response, taskcode)
//            },
//            Response.ErrorListener { error ->
//                if (progressDialog != null) progressDialog!!.dismiss()
//                try {
//                    val networkResponse = error.networkResponse
//                    var errorMessage: String? = "Unknown error"
//                    if (networkResponse == null) {
//                        if (error.javaClass == TimeoutError::class.java) {
//                            errorMessage = "Request timeout"
//                        } else if (error.javaClass == NoConnectionError::class.java) {
//                            errorMessage = "Failed to connect server"
//                        }
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
//                    } else {
//                        val result = String(networkResponse.data)
//                        try {
//                            val response = JSONObject(result)
//                            val errorObje = response.getJSONObject("error")
//                            val status = errorObje.getString("statusCode")
//                            val message = errorObje.getString("message")
//                            errorMessage = message
//                            if (status.compareTo("401") == 0) {
//                                //                                Intent i = new_image Intent(context, Login.class);
//                                //                                session.logoutUser();
//                                //                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//                                //                                context.startActivity(i);
//                                //                                ((Activity)context).finish();
//                            } else {
//                                Log.e("Error Message", message)
//                                //                                Toast.makeText(context,message, Toast.LENGTH_LONG).show();
//                                if (networkResponse.statusCode == 404) {
//                                    errorMessage = "Resource not found"
//                                } else if (networkResponse.statusCode == 401) {
//                                    errorMessage = "$message 401 Please login again"
//                                } else if (networkResponse.statusCode == 400) {
//                                    errorMessage = "$message Check your inputs"
//                                } else if (networkResponse.statusCode == 500) {
//                                    errorMessage = "$message Something is getting wrong"
//                                }
//                            }
//                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
//                        } catch (e: JSONException) {
//                            e.printStackTrace()
//                        }
//                    }
//                    //                    Log.i("Error", errorMessage);
//                    error.printStackTrace()
//                } finally {
//                }
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                return params
//            }
//
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                // header_param.put("Authorization", MyApplication.getInstance().getSid(Constants.AccessToken));
//                return HashMap()
//            }
//        }
//        stringRequest.retryPolicy = DefaultRetryPolicy(
//            60000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//        Volley.newRequestQueue(context).add(stringRequest)
//    }

    fun volleyStringRequestGet() {
        if (isProgress) {
            progressDialog =
                ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
            progressDialog!!.setCancelable(true)
        }
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                if (progressDialog != null) progressDialog!!.dismiss()
                webCompleteTask.onComplete(response, taskcode)
            },
            Response.ErrorListener { error ->
                if (progressDialog != null) progressDialog!!.dismiss()
                try {
                    val networkResponse = error.networkResponse
                    var errorMessage: String? = "Unknown error"
                    if (networkResponse == null) {
                        if (error.javaClass == TimeoutError::class.java) {
                            errorMessage = "Request timeout"
                        } else if (error.javaClass == NoConnectionError::class.java) {
                            errorMessage = "Failed to connect server"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        val result = String(networkResponse.data)
                        try {
                            val response = JSONObject(result)
                            val errorObje = response.getJSONObject("error")
                            val status = errorObje.getString("statusCode")
                            val message = errorObje.getString("message")
                            errorMessage = message
                            if (status.compareTo("401") == 0) {
                                //                                Utils.showMessage(context, message);

                                //                                Intent i = new_image Intent(context, Login.class);
                                //                                session.logoutUser();
                                //                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                //                                context.startActivity(i);
                                //                                ((Activity)context).finish();
                            } else {
                                Log.e("Error Message", message)
                                //  Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found"
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = "$message 401 Please login again"
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = "$message Check your inputs"
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = "$message Something is getting wrong"
                                }
                            }
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    //                    Log.i("Error", errorMessage);
                    error.printStackTrace()
                } finally {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                //                header_param.put("Authorization", Utils.getInstance(context).getAccessToken());
//                header_param.put("ln", Utils.getLangId(context, RequestCode.LangId));
                return HashMap()
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }

    fun volleyStringRequestGetLn() {
        if (isProgress) {
            progressDialog =
                ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
            progressDialog!!.setCancelable(true)
        }
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                if (progressDialog != null) progressDialog!!.dismiss()
                webCompleteTask.onComplete(response, taskcode)
            },
            Response.ErrorListener { error ->
                if (progressDialog != null) progressDialog!!.dismiss()
                try {
                    val networkResponse = error.networkResponse
                    var errorMessage: String? = "Unknown error"
                    if (networkResponse == null) {
                        if (error.javaClass == TimeoutError::class.java) {
                            errorMessage = "Request timeout"
                        } else if (error.javaClass == NoConnectionError::class.java) {
                            errorMessage = "Failed to connect server"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        val result = String(networkResponse.data)
                        try {
                            val response = JSONObject(result)
                            val errorObje = response.getJSONObject("error")
                            val status = errorObje.getString("statusCode")
                            val message = errorObje.getString("message")
                            errorMessage = message
                            if (status.compareTo("401") == 0) {
                                //                                Utils.showMessage(context, message);

                                //                                Intent i = new_image Intent(context, Login.class);
                                //                                session.logoutUser();
                                //                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                //                                context.startActivity(i);
                                //                                ((Activity)context).finish();
                            } else {
                                Log.e("Error Message", message)
                                //                                Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found"
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = "$message 401 Please login again"
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = "$message Check your inputs"
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = "$message Something is getting wrong"
                                }
                            }
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    //                    Log.i("Error", errorMessage);
                    error.printStackTrace()
                } finally {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                //                header_param.put("Authorization",Utils.getInstance(context).getAccessToken());
//                header_param.put("ln", Utils.getLangId(context, RequestCode.LangId));
                return HashMap()
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }

    fun volleyStringRequestGetWithoutProgressBar() {
//        if (isProgress) {
//            progressDialog = ProgressDialog.show(context, "",  context.getString(R.string.api_hiting));
//            progressDialog.setCancelable(true);
//        }
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response -> //                if (progressDialog != null)
                //                    progressDialog.dismiss();
                webCompleteTask.onComplete(response, taskcode)
            },
            Response.ErrorListener { error ->
                //                if (progressDialog != null)
                //                    progressDialog.dismiss();
                try {
                    val networkResponse = error.networkResponse
                    var errorMessage: String? = "Unknown error"
                    if (networkResponse == null) {
                        if (error.javaClass == TimeoutError::class.java) {
                            errorMessage = "Request timeout"
                        } else if (error.javaClass == NoConnectionError::class.java) {
                            errorMessage = "Failed to connect server"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        val result = String(networkResponse.data)
                        try {
                            val response = JSONObject(result)
                            val errorObje = response.getJSONObject("error")
                            val status = errorObje.getString("statusCode")
                            val message = errorObje.getString("message")
                            errorMessage = message
                            if (status.compareTo("401") == 0) {

                                //                                Intent i = new_image Intent(context, Login.class);
                                //                                session.logoutUser();
                                //                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                //                                context.startActivity(i);
                                //                                ((Activity)context).finish();
                            } else {
                                Log.e("Error Message", message)
                                //                                Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found"
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = "$message 401 Please login again"
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = "$message Check your inputs"
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = "$message Something is getting wrong"
                                }
                            }
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    //                    Log.i("Error", errorMessage);
                    error.printStackTrace()
                } finally {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                //                header_param.put("Authorization", Utils.getInstance(context).getAccessToken());
//                header_param.put("ln", Utils.getLangId(context, RequestCode.LangId));
                return HashMap()
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }

    fun volleyStringRequestLogOut() {
        if (isProgress) {
            progressDialog =
                ProgressDialog.show(context, "", context.getString(R.string.api_hiting))
            progressDialog!!.setCancelable(true)
        }
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (progressDialog != null) progressDialog!!.dismiss()
                webCompleteTask.onComplete(response, taskcode)
            },
            Response.ErrorListener { error ->
                if (progressDialog != null) progressDialog!!.dismiss()
                try {
                    val networkResponse = error.networkResponse
                    var errorMessage = "Unknown error"
                    if (networkResponse == null) {
                        if (error.javaClass == TimeoutError::class.java) {
                            errorMessage = "Request timeout"
                        } else if (error.javaClass == NoConnectionError::class.java) {
                            errorMessage = "Failed to connect server"
                        }
                    } else {
                        val result = String(networkResponse.data)
                        try {
                            val response = JSONObject(result)
                            val jobj = JSONObject(response.getString("error"))
                            val message = jobj.getString("message")
                            val status = jobj.getString("statusCode")
                            if (status.compareTo("401") == 0) {
                                //                                Utils.showMessage(context, message);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                //                                Intent i = new_image Intent(context, Login.class);
                                //                                session.logoutUser();
                                //                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                //                                context.startActivity(i);
                                //                                ((Activity)context).finish();
                            } else {
                                Log.e("Error Message", message)
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found"
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = "$message 401 Please login again"
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = "$message Check your inputs"
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = "$message Something is getting wrong"
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    //                    Log.i("Error", errorMessage);
                    error.printStackTrace()
                } finally {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                //                header_param.put("Authorization", Utils.getInstance(context).getRegPeopleId());
//                header_param.put("ln", Utils.getLangId(context, RequestCode.LangId));
                return HashMap()
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(stringRequest)
    }
}