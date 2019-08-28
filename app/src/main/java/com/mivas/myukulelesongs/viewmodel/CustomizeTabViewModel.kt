package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.AndroidViewModel
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.model.ChordData
import com.mivas.myukulelesongs.repository.TabRepository
import com.mivas.myukulelesongs.util.ChordDetector
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.DimensionUtils
import com.mivas.myukulelesongs.util.Prefs

class CustomizeTabViewModel(application: Application) : AndroidViewModel(application) {

    fun getTextSize() = Prefs.getInt(Constants.PREF_TAB_TEXT_SIZE, Constants.DEFAULT_TAB_TEXT_SIZE)
    fun getTextColor() = Prefs.getInt(Constants.PREF_TAB_TEXT_COLOR, Constants.DEFAULT_TAB_TEXT_COLOR)
    fun getChordColor() = Prefs.getInt(Constants.PREF_TAB_CHORD_COLOR, Constants.DEFAULT_TAB_CHORD_COLOR)
    fun getBackgroundColor() = Prefs.getInt(Constants.PREF_TAB_BACKGROUND_COLOR, Constants.DEFAULT_TAB_BACKGROUND_COLOR)

    fun setTextSize(textSize: Int) = Prefs.putInt(Constants.PREF_TAB_TEXT_SIZE, textSize)
    fun setTextColor(textColor: Int) = Prefs.putInt(Constants.PREF_TAB_TEXT_COLOR, textColor)
    fun setChordColor(chordColor: Int) = Prefs.putInt(Constants.PREF_TAB_CHORD_COLOR, chordColor)
    fun setBackgroundColor(backgroundColor: Int) = Prefs.putInt(Constants.PREF_TAB_BACKGROUND_COLOR, backgroundColor)

    fun createBackground(backgroundColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = DimensionUtils.dpToPx(6).toFloat()
            setColor(backgroundColor)
        }
    }

    fun reset() {
        setTextSize(Constants.DEFAULT_TAB_TEXT_SIZE)
        setTextColor(Constants.DEFAULT_TAB_TEXT_COLOR)
        setChordColor(Constants.DEFAULT_TAB_CHORD_COLOR)
        setBackgroundColor(Constants.DEFAULT_TAB_BACKGROUND_COLOR)
    }

}