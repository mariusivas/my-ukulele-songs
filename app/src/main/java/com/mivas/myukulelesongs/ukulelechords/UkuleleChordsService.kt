package com.mivas.myukulelesongs.ukulelechords

import com.mivas.myukulelesongs.ukulelechords.model.ChordsXml
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