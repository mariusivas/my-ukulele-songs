package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.AddEditSongRepository
import com.mivas.myukulelesongs.rest.model.ChordsXml
import com.mivas.myukulelesongs.rest.service.UkuleleChordsClient
import com.mivas.myukulelesongs.util.ChordHelper
import com.mivas.myukulelesongs.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChordViewModel(application: Application, chord: String) : AndroidViewModel(application) {

    private var xmlData: MutableLiveData<ChordsXml> = MutableLiveData()

    init {
        val ucData = ChordHelper.getUCChordData(chord)
        val call = UkuleleChordsClient.service.getChordData(Constants.API_KEY_UKULELE_CHORDS, ucData.chord, ucData.variation)
        call.enqueue(object : Callback<ChordsXml> {

            override fun onResponse(call: Call<ChordsXml>, response: Response<ChordsXml>) {
                xmlData.value = response.body()
            }

            override fun onFailure(call: Call<ChordsXml>, t: Throwable) = Unit

        })
    }

    fun getXmlData() = xmlData
}