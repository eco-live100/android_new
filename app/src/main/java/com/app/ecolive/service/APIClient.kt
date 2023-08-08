package com.app.ecolive.service

import android.util.Log
import com.app.ecolive.utils.PreferenceKeeper
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object APIClient {
//    const val BASE_URL = "https://ecolive.ezxdemo.com/server/api1/"
    const val BASE_URL = "https://api.ecolive.global/api1/"

    fun makeRetrofitServiceHeader(): APIInterface {
        val token = PreferenceKeeper.instance.bearerTokenSave
        Log.d("ok", "Bearer $token")
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        val client = httpClient
            .retryOnConnectionFailure(true)
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(APIInterface::class.java)


    }


}