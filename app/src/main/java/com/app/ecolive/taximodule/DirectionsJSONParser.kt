package com.app.ecolive.taximodule

import com.app.ecolive.taximodule.model.RouteModel
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by mukku on 27/11/15.
 */
class DirectionsJSONParser {
    var routeList: ArrayList<RouteModel> = ArrayList()
    @JvmName("getRouteList1")
    fun getRouteList(): ArrayList<RouteModel> {
        return routeList
    }

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {

        val routes: ArrayList<List<HashMap<String, String>>> = ArrayList()
        var jRoutes: JSONArray? = null
        var jLegs: JSONArray? = null
        var jSteps: JSONArray? = null
        try {
            jRoutes = jObject.getJSONArray("routes")
            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path = ArrayList<HashMap<String, String>>()
                val routeModel = RouteModel()
                routeModel.selectedRoute = (
                    jRoutes.getJSONObject(i).optJSONObject("overview_polyline").optString("points")
                )
                for (j in 0 until jLegs.length()) {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    val distance = (jLegs[j] as JSONObject).optJSONObject("distance")
                    val duration = (jLegs[j] as JSONObject).optJSONObject("duration")
                    routeModel.distance=(distance.optString("text"))
                    routeModel.valueDistance=(distance.optString("value"))
                    routeModel.duration=(duration.optString("text"))
                    routeModel.valueDuration=(duration.optString("value"))
                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        var polyline = ""
                        polyline =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list = decodePoly(polyline)
                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm["lat"] = (list[l] as LatLng).latitude.toString()
                            hm["lng"] = (list[l] as LatLng).longitude.toString()
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                }
                routeList.add(routeModel)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
        }
        return routes
    }

    companion object {
        /**
         * Method to decode polyline points
         * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        fun decodePoly(encoded: String): List<*> {
            val poly = ArrayList<Any?>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0
            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat
                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng
                val p = LatLng(
                    lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5
                )
                poly.add(p)
            }
            return poly
        }
    }
}