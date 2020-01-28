package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.ukulelechords.model.ChordsXml
import com.mivas.myukulelesongs.ukulelechords.UkuleleChordsClient
import com.mivas.myukulelesongs.util.ChordHelper
import com.mivas.myukulelesongs.util.Constants
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * Repository for [com.mivas.myukulelesongs.viewmodel.ChordInfoViewModel].
 */
class ChordInfoRepository {

    /**
     * Retrieves a [ChordsXml] from ukulele-chords.com for the given chord
     *
     * @param chord The chord for which to retrieve the data
     */
    suspend fun getChordsXml(chord: String) = withContext(IO) {
        val ucData = ChordHelper.getUCChordData(chord)
        val call = UkuleleChordsClient.service.getChordData(Constants.API_KEY_UKULELE_CHORDS, ucData.chord, ucData.chordType)
        call.execute().body() ?: ChordsXml()
    }

}