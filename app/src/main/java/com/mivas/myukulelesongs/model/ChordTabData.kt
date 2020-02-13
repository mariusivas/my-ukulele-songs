package com.mivas.myukulelesongs.model

import android.text.SpannableStringBuilder

/**
 * Data about a song to be displayed in [com.mivas.myukulelesongs.ui.activity.TabActivity].
 *
 * @param spannableBuilder Colored text ready to be displayed in a TextView
 * @param chords List of all chords detected in a song
 * @param notes List of all notes detected in a song
 */
data class ChordTabData(
    val spannableBuilder: SpannableStringBuilder,
    val chords: List<String>,
    val notes: List<String>
)