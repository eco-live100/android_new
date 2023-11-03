package com.app.ecolive.service

import com.app.ecolive.BuildConfig


data class ApiSampleResource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(httpCode: Int, msg: String, data: T?): ApiSampleResource<T> {
            var message = ""
            when (httpCode) {
                200 -> message = "Successfully"
                201 -> message = msg
                204 -> message = "Sorry Records Not Found."
                409 -> message = msg
            }

            return if (BuildConfig.DEBUG) {
                ApiSampleResource(Status.SUCCESS, data, message)
            } else {
                ApiSampleResource(Status.SUCCESS, data, msg)
            }

        }

        fun <T> error(httpCode: Int, msg: String, data: T?): ApiSampleResource<T> {
            var message = ""
            when (httpCode) {
                // 201->message = "The request has been fulfilled and has resulted in one or more new resources being created"

//                502 -> message = "No Internet connection,Please check Internet connection."
//                500 -> message = "Server seems busy.Please try after sometime."
//                400 -> message = "Something went wrong."
//                201 -> message = "Your are registered successfully"
                502 -> message = msg
                500 -> message = msg
                400 -> message = msg
                //201 -> message = msg
                404 -> message = msg
                401 -> message = msg
                205 -> message = msg
                204 -> message = msg //empty res
                408 -> message = msg
                409 -> message = msg
                504 -> message = "Data Parsing Error"
                else -> {
                    message = "ApiSampleResource Status code not found"
                }


            }

            return if (BuildConfig.DEBUG) {
                ApiSampleResource(Status.ERROR, data, message)
            } else {
                ApiSampleResource(Status.ERROR, data, msg)
            }

        }


    }

}