package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.ukulelechords.model.ChordsXml
import com.mivas.myukulelesongs.ukulelechords.UkuleleChordsClient
import com.mivas.myukulelesongs.util.ChordHelper
import com.mivas.myukulelesongs.util.Constants
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class ChordRepository {

    suspend fun getChordsXml(chord: String) = withContext(IO) {
        val ucData = ChordHelper.getUCChordData(chord)
        val call = UkuleleChordsClient.service.getChordData(Constants.API_KEY_UKULELE_CHORDS, ucData.chord, ucData.variation)
        call.execute().body() ?: ChordsXml()
    }

}