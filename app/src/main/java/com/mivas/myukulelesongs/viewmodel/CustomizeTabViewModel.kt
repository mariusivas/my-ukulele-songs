package com.mivas.myukulelesongs.viewmodel

import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.ViewModel
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.DimensionUtils
import com.mivas.myukulelesongs.util.Prefs

/**
 * ViewModel for [com.mivas.myukulelesongs.ui.activity.CustomizeTabActivity].
 */
class CustomizeTabViewModel : ViewModel() {

    fun getTextSize() = Prefs.getInt(Constants.PREF_TAB_TEXT_SIZE, Constants.DEFAULT_CHORDS_SONG_TEXT_SIZE)
    fun getTextColor() = Prefs.getInt(Constants.PREF_TAB_TEXT_COLOR, Constants.DEFAULT_CHORDS_SONG_TEXT_COLOR)
    fun getChordColor() = Prefs.getInt(Constants.PREF_TAB_CHORD_COLOR, Constants.DEFAULT_CHORDS_SONG_CHORD_COLOR)
    fun getBackgroundColor() = Prefs.getInt(Constants.PREF_TAB_BACKGROUND_COLOR, Constants.DEFAULT_CHORDS_SONG_BACKGROUND_COLOR)
    fun getHeaderColor() = Prefs.getInt(Constants.PREF_TAB_HEADER_COLOR, Constants.DEFAULT_TAB_SONG_HEADER_COLOR)
    fun getLineColor() = Prefs.getInt(Constants.PREF_TAB_LINE_COLOR, Constants.DEFAULT_TAB_SONG_LINE_COLOR)
    fun getNumbersColor() = Prefs.getInt(Constants.PREF_TAB_NUMBERS_COLOR, Constants.DEFAULT_TAB_SONG_NUMBERS_COLOR)

    fun setTextSize(textSize: Int) = Prefs.putInt(Constants.PREF_TAB_TEXT_SIZE, textSize)
    fun setTextColor(textColor: Int) = Prefs.putInt(Constants.PREF_TAB_TEXT_COLOR, textColor)
    fun setChordColor(chordColor: Int) = Prefs.putInt(Constants.PREF_TAB_CHORD_COLOR, chordColor)
    fun setBackgroundColor(backgroundColor: Int) = Prefs.putInt(Constants.PREF_TAB_BACKGROUND_COLOR, backgroundColor)
    fun setHeaderColor(textColor: Int) = Prefs.putInt(Constants.PREF_TAB_HEADER_COLOR, textColor)
    fun setLineColor(chordColor: Int) = Prefs.putInt(Constants.PREF_TAB_LINE_COLOR, chordColor)
    fun setNumbersColor(chordColor: Int) = Prefs.putInt(Constants.PREF_TAB_NUMBERS_COLOR, chordColor)

    /**
     * Creates a rounded corners colored shape.
     *
     * @param backgroundColor The color of the shape
     */
    fun createBackground(backgroundColor: Int) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = DimensionUtils.dpToPx(6).toFloat()
        setColor(backgroundColor)
    }

    /**
     * Resets all customization to default settings.
     */
    fun reset() {
        setTextSize(Constants.DEFAULT_CHORDS_SONG_TEXT_SIZE)
        setTextColor(Constants.DEFAULT_CHORDS_SONG_TEXT_COLOR)
        setChordColor(Constants.DEFAULT_CHORDS_SONG_CHORD_COLOR)
        setBackgroundColor(Constants.DEFAULT_CHORDS_SONG_BACKGROUND_COLOR)
        setHeaderColor(Constants.DEFAULT_TAB_SONG_HEADER_COLOR)
        setLineColor(Constants.DEFAULT_TAB_SONG_LINE_COLOR)
        setNumbersColor(Constants.DEFAULT_TAB_SONG_NUMBERS_COLOR)
    }

}