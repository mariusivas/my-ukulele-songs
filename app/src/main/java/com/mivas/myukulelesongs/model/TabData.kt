package com.mivas.myukulelesongs.model

import android.text.SpannableStringBuilder

/**
 * Data about a tab song to be displayed in [com.mivas.myukulelesongs.ui.activity.TabActivity].
 *
 * @param spannableBuilder Colored text ready to be displayed in a TextView
 * @param notes List of all notes detected in a tab song
 */
data class TabData(
        val spannableBuilder: SpannableStringBuilder,
        val notes: List<String>
)