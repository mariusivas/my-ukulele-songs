package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.model.ChordData
import com.mivas.myukulelesongs.repository.TabRepository
import com.mivas.myukulelesongs.util.ChordHelper
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import com.mivas.myukulelesongs.util.TransposeHelper

class TabViewModel(application: Application, songId: Long) : AndroidViewModel(application) {

    private var tabRepository = TabRepository(application)
    private var song = tabRepository.getSongById(songId)
    var transposedText = MutableLiveData<String>().apply { postValue("") }
    var scrollRunning = false

    fun getSong() = song
    fun updateSong(song: Song) = tabRepository.update(song)

    private fun getChordColor() = Prefs.getInt(Constants.PREF_TAB_CHORD_COLOR, Constants.DEFAULT_TAB_CHORD_COLOR)
    fun getTextSize() = Prefs.getInt(Constants.PREF_TAB_TEXT_SIZE, Constants.DEFAULT_TAB_TEXT_SIZE)
    fun getTextColor() = Prefs.getInt(Constants.PREF_TAB_TEXT_COLOR, Constants.DEFAULT_TAB_TEXT_COLOR)
    fun getBackgroundColor() = Prefs.getInt(Constants.PREF_TAB_BACKGROUND_COLOR, Constants.DEFAULT_TAB_BACKGROUND_COLOR)

    fun getChordData(tab: String): ChordData {
        val chordColor = getChordColor()
        val songChords = mutableSetOf<String>()
        val builder = SpannableStringBuilder()
        val lines = tab.split("\n")
        lines.forEach { line ->
            if (ChordHelper.isChordLine(line)) {
                val spannable = SpannableString(line)
                spannable.setSpan(ForegroundColorSpan(chordColor), 0, line.length, 0)
                builder.append(spannable)
                val lineChords = ChordHelper.getChordsInLine(line)
                songChords.addAll(lineChords)

            } else {
                builder.append(line)
            }
            builder.append("\n")
        }
        return ChordData(builder, songChords)
    }

    fun transpose(plus: Boolean) {
        val preferSharp = Prefs.getBoolean(Constants.PREF_PREFER_SHARP)
        val text = if (transposedText.value!!.isNotEmpty()) transposedText.value!! else song.value!!.tab
        val transposed = TransposeHelper.transposeSong(text, plus, preferSharp)
        transposedText.value = transposed
    }

}