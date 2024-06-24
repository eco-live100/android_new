package com.app.ecolive.POJO

   import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClientGeoCode {

//    private const val URL = "http://192.168.100.159:8000/api/"
    private const val URL = "https://maps.googleapis.com/maps/api/"

    //CREATE HTTP CLIENT
    //  var interceptor =HttpLoggingInterceptor.Level.BODY
    var interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

     val okHttpClienttest = OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("Authorization", "Bearer " + "" )
                return@Interceptor chain.proceed(builder.build())
            }
        )
    }

    var client: OkHttpClient = okHttpClienttest
        .retryOnConnectionFailure(true)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    //retrofit builder
    private val builder = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)

    //create retrofit Instance
    private val retrofit = builder.build()

    //we will use this class to create an anonymous inner class function that
    //implements Country service Interface

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

}