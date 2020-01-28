package com.mivas.myukulelesongs.model

import android.text.SpannableStringBuilder

/**
 * Data about a chords song to be displayed in [com.mivas.myukulelesongs.ui.activity.TabActivity].
 *
 * @param spannableBuilder Colored text ready to be displayed in a TextView
 * @param chords List of all chords detected in a chords song
 */
data class ChordTabData(
    val spannableBuilder: SpannableStringBuilder,
    val chords: List<String>,
    val notes: List<String>
)