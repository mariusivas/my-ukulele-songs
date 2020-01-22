package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.DimensionUtils
import com.mivas.myukulelesongs.util.Prefs

class CustomizeChordsSongViewModel : ViewModel() {

    fun getTextSize() = Prefs.getInt(Constants.PREF_CHORDS_SONG_TEXT_SIZE, Constants.DEFAULT_CHORDS_SONG_TEXT_SIZE)
    fun getTextColor() = Prefs.getInt(Constants.PREF_CHORDS_SONG_TEXT_COLOR, Constants.DEFAULT_CHORDS_SONG_TEXT_COLOR)
    fun getChordColor() = Prefs.getInt(Constants.PREF_CHORDS_SONG_CHORD_COLOR, Constants.DEFAULT_CHORDS_SONG_CHORD_COLOR)
    fun getBackgroundColor() = Prefs.getInt(Constants.PREF_CHORDS_SONG_BACKGROUND_COLOR, Constants.DEFAULT_CHORDS_SONG_BACKGROUND_COLOR)

    fun setTextSize(textSize: Int) = Prefs.putInt(Constants.PREF_CHORDS_SONG_TEXT_SIZE, textSize)
    fun setTextColor(textColor: Int) = Prefs.putInt(Constants.PREF_CHORDS_SONG_TEXT_COLOR, textColor)
    fun setChordColor(chordColor: Int) = Prefs.putInt(Constants.PREF_CHORDS_SONG_CHORD_COLOR, chordColor)
    fun setBackgroundColor(backgroundColor: Int) = Prefs.putInt(Constants.PREF_CHORDS_SONG_BACKGROUND_COLOR, backgroundColor)

    fun createBackground(backgroundColor: Int) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = DimensionUtils.dpToPx(6).toFloat()
        setColor(backgroundColor)
    }

    fun reset() {
        setTextSize(Constants.DEFAULT_CHORDS_SONG_TEXT_SIZE)
        setTextColor(Constants.DEFAULT_CHORDS_SONG_TEXT_COLOR)
        setChordColor(Constants.DEFAULT_CHORDS_SONG_CHORD_COLOR)
        setBackgroundColor(Constants.DEFAULT_CHORDS_SONG_BACKGROUND_COLOR)
    }

}