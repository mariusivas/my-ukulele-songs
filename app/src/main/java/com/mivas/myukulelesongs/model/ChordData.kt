package com.mivas.myukulelesongs.model

import android.text.SpannableStringBuilder

data class ChordData(
        val spannableBuilder: SpannableStringBuilder,
        val chords: Set<String>,
        val allChords: List<String>
)