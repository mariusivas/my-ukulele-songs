package com.mivas.myukulelesongs.rest.service

import com.mivas.myukulelesongs.rest.model.ChordsXml
import retrofit2.Call
import retrofit2.http.*

interface UkuleleChordsService {

    @GET("get")
    fun getChordData(
        @Query("ak") apiKey: String,
        @Query("r") chord: String,
        @Query("typ") variation: String
    ) : Call<ChordsXml>


}