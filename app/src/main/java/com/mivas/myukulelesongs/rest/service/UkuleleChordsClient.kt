package com.mivas.myukulelesongs.rest.service

import com.mivas.myukulelesongs.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object UkuleleChordsClient {

    val service: UkuleleChordsService
    private const val URL_BASE = "https://ukulele-chords.com/"

    init {
        val interceptor = HttpLoggingInterceptor().apply { level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
        service = retrofit.create(UkuleleChordsService::class.java)
    }
}