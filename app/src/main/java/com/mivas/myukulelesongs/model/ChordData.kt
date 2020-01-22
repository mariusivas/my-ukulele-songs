package com.mivas.myukulelesongs.model

import android.text.SpannableStringBuilder

/**
 * Data about a chords song to be displayed in [com.mivas.myukulelesongs.ui.activity.SongActivity]
 *
 * @param spannableBuilder colored text ready to be displayed in a TextView
 * @param chords list of all chords detected in a chords song
 */
data class ChordData(
    val spannableBuilder: SpannableStringBuilder,
    val chords: List<String>
)