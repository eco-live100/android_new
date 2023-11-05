package com.app.ecolive.taximodule

import android.os.AsyncTask
import org.json.JSONObject
import java.util.*
/**
 * Created by mukku on 2/16/2018.
 */
class ParserTask(var callback: ParserCallback) :
    AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
    var parser: DirectionsJSONParser
    // Parsing the data in non-ui thread
    override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
        val jObject: JSONObject
        var routes: List<List<HashMap<String, String>>>? = null
        try {
            jObject = JSONObject(jsonData[0]!!)
            parser = DirectionsJSONParser()
            routes = parser.parse(jObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routes
    }

    override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
        callback.onParserResult(result!!, parser)
    }

    interface ParserCallback {
        fun onParserResult(
            result: List<List<HashMap<String, String>>>,
            parser: DirectionsJSONParser
        )
    }

    init {
        parser = DirectionsJSONParser()
    }


}