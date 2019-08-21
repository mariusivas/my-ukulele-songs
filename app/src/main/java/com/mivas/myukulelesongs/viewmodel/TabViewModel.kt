package com.mivas.myukulelesongs.viewmodel

import android.app.Application
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

class TabViewModel(application: Application, songId: Long) : AndroidViewModel(application) {

    private var tabRepository = TabRepository(application)
    private var song = tabRepository.getSongById(songId)

    fun getSong() = song

    fun getChordData(song: Song): ChordData {
        val chordColor = Prefs.getInt(Constants.PREF_TAB_CHORD_COLOR, Constants.DEFAULT_TAB_CHORD_COLOR)
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

    fun getDisplayChords(chordData: ChordData): String {
        var display = ""
        chordData.chords.forEach { display += "$it  " }
        return display
    }

}