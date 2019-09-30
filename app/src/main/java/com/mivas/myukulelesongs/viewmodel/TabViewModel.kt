package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.AndroidViewModel
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.model.ChordData
import com.mivas.myukulelesongs.repository.TabRepository
import com.mivas.myukulelesongs.util.ChordDetector
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import kotlinx.android.synthetic.main.activity_tab.*

class TabViewModel(application: Application, songId: Long) : AndroidViewModel(application) {

    private var tabRepository = TabRepository(application)
    private var song = tabRepository.getSongById(songId)
    var scrollRunning = false

    fun getSong() = song

    private fun getChordColor() = Prefs.getInt(Constants.PREF_TAB_CHORD_COLOR, Constants.DEFAULT_TAB_CHORD_COLOR)
    fun getTextSize() = Prefs.getInt(Constants.PREF_TAB_TEXT_SIZE, Constants.DEFAULT_TAB_TEXT_SIZE)
    fun getTextColor() = Prefs.getInt(Constants.PREF_TAB_TEXT_COLOR, Constants.DEFAULT_TAB_TEXT_COLOR)
    fun getBackgroundColor() = Prefs.getInt(Constants.PREF_TAB_BACKGROUND_COLOR, Constants.DEFAULT_TAB_BACKGROUND_COLOR)

    fun getChordData(song: Song): ChordData {
        val chordColor = getChordColor()
        val songChords = mutableSetOf<String>()
        val builder = SpannableStringBuilder()
        val lines = song.tab.split("\n")
        lines.forEach { line ->
            if (ChordDetector.isChordLine(line)) {
                val spannable = SpannableString(line)
                spannable.setSpan(ForegroundColorSpan(chordColor), 0, line.length, 0)
                builder.append(spannable)
                val lineChords = ChordDetector.getChordsInLine(line)
                songChords.addAll(lineChords)

            } else {
                builder.append(line)
            }
            builder.append("\n")
        }
        return ChordData(builder, songChords)
    }

}