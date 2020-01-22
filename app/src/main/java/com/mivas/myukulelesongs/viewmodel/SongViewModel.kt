package com.mivas.myukulelesongs.viewmodel

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.model.ChordData
import com.mivas.myukulelesongs.model.TabData
import com.mivas.myukulelesongs.repository.SongRepository
import com.mivas.myukulelesongs.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class SongViewModel(songId: Long) : ViewModel() {

    private val tabRepository = SongRepository()
    val song = tabRepository.getSongById(songId)
    var transposedText = MutableLiveData<String>().apply { value = "" }
    var scrollRunning = false

    fun updateSong(song: Song) = viewModelScope.launch(IO) { tabRepository.update(song) }

    fun getChordColor() = Prefs.getInt(Constants.PREF_CHORDS_SONG_CHORD_COLOR, Constants.DEFAULT_CHORDS_SONG_CHORD_COLOR)
    fun getTextSizeChords() = Prefs.getInt(Constants.PREF_CHORDS_SONG_TEXT_SIZE, Constants.DEFAULT_CHORDS_SONG_TEXT_SIZE)
    fun getTextColorChords() = Prefs.getInt(Constants.PREF_CHORDS_SONG_TEXT_COLOR, Constants.DEFAULT_CHORDS_SONG_TEXT_COLOR)
    fun getBackgroundColorChords() = Prefs.getInt(Constants.PREF_CHORDS_SONG_BACKGROUND_COLOR, Constants.DEFAULT_CHORDS_SONG_BACKGROUND_COLOR)
    fun getTextSizeTab() = Prefs.getInt(Constants.PREF_TAB_SONG_TEXT_SIZE, Constants.DEFAULT_TAB_SONG_TEXT_SIZE)
    fun getHeaderColorTab() = Prefs.getInt(Constants.PREF_TAB_SONG_HEADER_COLOR, Constants.DEFAULT_TAB_SONG_HEADER_COLOR)
    fun getLineColorTab() = Prefs.getInt(Constants.PREF_TAB_SONG_LINE_COLOR, Constants.DEFAULT_TAB_SONG_LINE_COLOR)
    fun getNumbersColorTab() = Prefs.getInt(Constants.PREF_TAB_SONG_NUMBERS_COLOR, Constants.DEFAULT_TAB_SONG_NUMBERS_COLOR)
    fun getBackgroundColorTab() = Prefs.getInt(Constants.PREF_TAB_SONG_BACKGROUND_COLOR, Constants.DEFAULT_TAB_SONG_BACKGROUND_COLOR)

    fun getChordData(tab: String): ChordData {
        val chordColor = getChordColor()
        val chords = mutableListOf<String>()
        val builder = SpannableStringBuilder()
        val lines = tab.split("\n")
        lines.forEach { line ->
            if (ChordHelper.isChordLine(line)) {
                val spannable = SpannableString(line)
                spannable.setSpan(ForegroundColorSpan(chordColor), 0, line.length, 0)
                builder.append(spannable)
                val lineChords = ChordHelper.getChordsInLine(line)
                chords.addAll(lineChords)
            } else {
                builder.append(line)
            }
            builder.append("\n")
        }
        if (builder.isNotEmpty()) builder.delete(builder.length - 1, builder.length)
        builder.append(".")
        return ChordData(builder, chords)
    }

    fun getTabData(text: String, maxCharsPerLine: Int): TabData {
        val tab = TabHelper.getFormattedTab(text, maxCharsPerLine)
        val headerColor = getHeaderColorTab()
        val lineColor = getLineColorTab()
        val numbersColor = getNumbersColorTab()
        val notes = mutableListOf<String>()
        val builder = SpannableStringBuilder()
        val headerChars = listOf("G", "C", "E", "A", "|")
        val lines = tab.split("\n")
        lines.forEach { line ->
            if (line.isNotEmpty()) {
                notes.addAll(TabHelper.getNotesInLine(line))
                val spannable = SpannableString(line)
                for (i in line.indices) {
                    val char = line[i].toString()
                    when {
                        headerChars.contains(char) -> spannable.setSpan(ForegroundColorSpan(headerColor), i, i + 1, 0)
                        TabHelper.isNumber(char) -> spannable.setSpan(ForegroundColorSpan(numbersColor), i, i + 1, 0)
                        else -> spannable.setSpan(ForegroundColorSpan(lineColor), i, i + 1, 0)
                    }
                }
                builder.append(spannable).append("\n")
            } else {
                builder.append("\n")
            }
        }
        if (builder.isNotEmpty()) builder.delete(builder.length - 1, builder.length)
        return TabData(builder, notes)
    }

    fun getMaxCharsPerLine(textView: TextView): Int {
        val layout = textView.layout
        var maxChars = 0
        for (i in 0 until layout.lineCount) {
            val line = layout.text.subSequence(layout.getLineStart(i), layout.getLineEnd(i)).toString().replace("\n", "")
            if (line.length > maxChars) maxChars = line.length
        }
        return maxChars - 2
    }

    fun transpose(plus: Boolean) {
        val preferSharp = Prefs.getBoolean(Constants.PREF_PREFER_SHARP)
        val text = if (transposedText.value!!.isNotEmpty()) transposedText.value!! else song.value!!.tab
        val transposed = TransposeHelper.transposeChordsSong(text, plus, preferSharp)
        transposedText.value = transposed
    }

}