package com.mivas.myukulelesongs.viewmodel

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.model.ChordTabData
import com.mivas.myukulelesongs.repository.TabRepository
import com.mivas.myukulelesongs.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class TabViewModel(songId: Long) : ViewModel() {

    private val tabRepository = TabRepository()
    val song = tabRepository.getSongById(songId)

    fun updateSong(song: Song) = viewModelScope.launch(IO) { tabRepository.update(song) }

    fun getChordColor() = Prefs.getInt(Constants.PREF_TAB_CHORD_COLOR, Constants.DEFAULT_CHORDS_SONG_CHORD_COLOR)
    fun getTextSizeChords() = Prefs.getInt(Constants.PREF_TAB_TEXT_SIZE, Constants.DEFAULT_CHORDS_SONG_TEXT_SIZE)
    fun getTextColorChords() = Prefs.getInt(Constants.PREF_TAB_TEXT_COLOR, Constants.DEFAULT_CHORDS_SONG_TEXT_COLOR)
    fun getBackgroundColorChords() = Prefs.getInt(Constants.PREF_TAB_BACKGROUND_COLOR, Constants.DEFAULT_CHORDS_SONG_BACKGROUND_COLOR)
    fun getHeaderColorTab() = Prefs.getInt(Constants.PREF_TAB_HEADER_COLOR, Constants.DEFAULT_TAB_SONG_HEADER_COLOR)
    fun getLineColorTab() = Prefs.getInt(Constants.PREF_TAB_LINE_COLOR, Constants.DEFAULT_TAB_SONG_LINE_COLOR)
    fun getNumbersColorTab() = Prefs.getInt(Constants.PREF_TAB_NUMBERS_COLOR, Constants.DEFAULT_TAB_SONG_NUMBERS_COLOR)

    fun getChordsTabData(tab: String, maxCharsPerLine: Int): ChordTabData {
        val chords = mutableListOf<String>()
        val notes = mutableListOf<String>()
        val chordColor = getChordColor()
        val tabHeaderColor = getHeaderColorTab()
        val tabLineColor = getLineColorTab()
        val tabNumbersColor = getNumbersColorTab()
        val builder = SpannableStringBuilder()
        val formattedTab = TabHelper.getAlignedTab(tab, maxCharsPerLine)
        val lines = formattedTab.split("\n")
        lines.forEach { line ->
            if (ChordHelper.isChordLine(line)) {
                val spannable = SpannableString(line)
                spannable.setSpan(ForegroundColorSpan(chordColor), 0, line.length, 0)
                builder.append(spannable)
                val lineChords = ChordHelper.getChordsInLine(line)
                chords.addAll(lineChords)
            } else if (TabHelper.isTabLine(line)) {
                notes.addAll(TabHelper.getNotesInLine(line))
                val spannable = SpannableString(line)
                for (i in line.indices) {
                    val char = line[i]
                    when {
                        TabHelper.headerChars.contains(char) -> spannable.setSpan(ForegroundColorSpan(tabHeaderColor), i, i + 1, 0)
                        char.toString().isNumber() -> spannable.setSpan(ForegroundColorSpan(tabNumbersColor), i, i + 1, 0)
                        else -> spannable.setSpan(ForegroundColorSpan(tabLineColor), i, i + 1, 0)
                    }
                }
                builder.append(spannable)
            } else {
                builder.append(line)
            }
            builder.append("\n")
        }
        if (builder.isNotEmpty()) builder.delete(builder.length - 1, builder.length)
        return ChordTabData(builder, chords, notes)
    }

    fun getSongKey(chordTabData: ChordTabData): String {
        val chordsKey = KeyHelper.findKeyFromChords(chordTabData.chords)
        val tabKey = KeyHelper.findKeyFromTab(chordTabData.notes)
        return when {
            chordsKey.isNotEmpty() && tabKey.isEmpty() -> chordsKey
            chordsKey.isEmpty() && tabKey.isNotEmpty() -> tabKey
            chordsKey == tabKey -> chordsKey
            else -> "$chordsKey & $tabKey"
        }
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

}