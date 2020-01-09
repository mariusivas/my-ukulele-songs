package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mivas.myukulelesongs.repository.ChordRepository

class ChordViewModel(chord: String) : ViewModel() {

    private val chordRepository = ChordRepository()

    val xmlData = liveData { emit(chordRepository.getChordsXml(chord)) }

    fun getAlternativeUrls() = xmlData.value?.let { xml ->
        val urls = xml.chords.map { it.miniDiagram }.toMutableList()
        if (urls.isNotEmpty()) urls.removeAt(0)
        urls
    } ?: listOf<String>()

}