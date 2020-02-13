package com.mivas.myukulelesongs.ukulelechords

import com.mivas.myukulelesongs.ukulelechords.model.ChordsXml
import retrofit2.Call
import retrofit2.http.*

/**
 * Requests made to ukulele-chords.com
 */
interface UkuleleChordsService {

    /**
     * Retrieves the chord data for a given chord.
     *
     * @param apiKey API key for ukulele-chords.com
     * @param r The chord. Must not use sharps, only flats
     * @param chordType The type of the chord. m, 7, etc.
     */
    @GET("get")
    fun getChordData(
        @Query("ak") apiKey: String,
        @Query("r") chord: String,
        @Query("typ") chordType: String
    ) : Call<ChordsXml>


}