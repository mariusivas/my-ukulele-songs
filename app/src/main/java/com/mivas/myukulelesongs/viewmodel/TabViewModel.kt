package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.AddEditSongRepository
import com.mivas.myukulelesongs.repository.TabRepository
import com.mivas.myukulelesongs.util.ChordDetector

class TabViewModel(application: Application, songId: Long) : AndroidViewModel(application) {

    private var tabRepository = TabRepository(application)
    private var song = tabRepository.getSongById(songId)

    fun getSong() = song

    fun getSpannable(song: Song): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val lines = song.tab.split("\n")
        lines.forEach { line ->
            if (ChordDetector.isChordLine(line)) {
                val spannable = SpannableString(line)
                spannable.setSpan(ForegroundColorSpan(Color.RED), 0, line.length, 0)
                builder.append(spannable)
            } else {
                builder.append(line)
            }
            builder.append("\n")
        }
        return builder
    }

}