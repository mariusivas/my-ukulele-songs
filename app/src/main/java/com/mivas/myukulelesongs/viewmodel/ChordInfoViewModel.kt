package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mivas.myukulelesongs.repository.ChordInfoRepository

/**
 * ViewModel for [com.mivas.myukulelesongs.ui.activity.ChordInfoActivity].
 */
class ChordInfoViewModel(chord: String) : ViewModel() {

    private val chordRepository = ChordInfoRepository()

    val xmlData = liveData { emit(chordRepository.getChordsXml(chord)) }

    /**
     * Returns the list of alternatives.
     */
    fun getAlternativeUrls() = xmlData.value?.let { xml ->
        val urls = xml.chords.map { it.miniDiagram }.toMutableList()
        if (urls.isNotEmpty()) urls.removeAt(0)
        urls
    } ?: listOf<String>()

}