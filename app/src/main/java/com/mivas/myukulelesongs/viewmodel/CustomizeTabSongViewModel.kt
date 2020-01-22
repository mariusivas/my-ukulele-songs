package com.mivas.myukulelesongs.viewmodel

import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.ViewModel
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.DimensionUtils
import com.mivas.myukulelesongs.util.Prefs

class CustomizeTabSongViewModel() : ViewModel() {

    fun getTextSize() = Prefs.getInt(Constants.PREF_TAB_SONG_TEXT_SIZE, Constants.DEFAULT_TAB_SONG_TEXT_SIZE)
    fun getHeaderColor() = Prefs.getInt(Constants.PREF_TAB_SONG_HEADER_COLOR, Constants.DEFAULT_TAB_SONG_HEADER_COLOR)
    fun getLineColor() = Prefs.getInt(Constants.PREF_TAB_SONG_LINE_COLOR, Constants.DEFAULT_TAB_SONG_LINE_COLOR)
    fun getNumbersColor() = Prefs.getInt(Constants.PREF_TAB_SONG_NUMBERS_COLOR, Constants.DEFAULT_TAB_SONG_NUMBERS_COLOR)
    fun getBackgroundColor() = Prefs.getInt(Constants.PREF_TAB_SONG_BACKGROUND_COLOR, Constants.DEFAULT_TAB_SONG_BACKGROUND_COLOR)

    fun setTextSize(textSize: Int) = Prefs.putInt(Constants.PREF_TAB_SONG_TEXT_SIZE, textSize)
    fun setHeaderColor(textColor: Int) = Prefs.putInt(Constants.PREF_TAB_SONG_HEADER_COLOR, textColor)
    fun setLineColor(chordColor: Int) = Prefs.putInt(Constants.PREF_TAB_SONG_LINE_COLOR, chordColor)
    fun setNumbersColor(chordColor: Int) = Prefs.putInt(Constants.PREF_TAB_SONG_NUMBERS_COLOR, chordColor)
    fun setBackgroundColor(backgroundColor: Int) = Prefs.putInt(Constants.PREF_TAB_SONG_BACKGROUND_COLOR, backgroundColor)

    fun createBackground(backgroundColor: Int) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = DimensionUtils.dpToPx(6).toFloat()
        setColor(backgroundColor)
    }

    fun reset() {
        setTextSize(Constants.DEFAULT_TAB_SONG_TEXT_SIZE)
        setHeaderColor(Constants.DEFAULT_TAB_SONG_HEADER_COLOR)
        setLineColor(Constants.DEFAULT_TAB_SONG_LINE_COLOR)
        setNumbersColor(Constants.DEFAULT_TAB_SONG_NUMBERS_COLOR)
        setBackgroundColor(Constants.DEFAULT_TAB_SONG_BACKGROUND_COLOR)
    }

}