package com.app.ecolive.service

import com.app.ecolive.BuildConfig


data class Resource<out T>(val status: Status, val data: T?, val message: String?,val imgPath: String?) {

    companion object {

      /*  fun <T> success(data: T?,imgpath: String): Resource<T> {
            return Resource(Status.SUCCESS, data, null,imgpath)
        }*/

        fun <T> success(httpCode:Int,msg: String,data: T?,imgpath: String): Resource<T> {
            var message = ""
            when(httpCode){
                200-> message = "Successfully"
                204-> message = "Sorry Records Not Found."

            }

            return if(BuildConfig.DEBUG){
                Resource(Status.SUCCESS, data, message,imgpath)
            }else{
                Resource(Status.SUCCESS, data,msg,imgpath)
            }

        }

        fun <T> error(httpCode:Int,msg: String, data: T?): Resource<T> {
            var message = ""
            when(httpCode){
                502-> message = "No Internet connection,Please check Internet connection."
                500-> message = "Server seems busy.Please try after sometime."
                400-> message = "Something went wrong."
                201-> message = "Your are registered successfully"
                401-> message = "No Internet connection,Please check Internet connection."
                204-> message = "No Internet connection,Please check Internet connection."
                409-> message = "No Internet connection,Please check Internet connection."

            }

            return if(BuildConfig.DEBUG){
                Resource(Status.ERROR, data, message,null)
            }else{
                Resource(Status.ERROR, data, msg,null)
            }

        }

//        fun <T> error(msg: String, data: T?,): Resource<T> {
//            return Resource(Status.ERROR, data,msg,null)
//        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null,null)
        }

    }

}