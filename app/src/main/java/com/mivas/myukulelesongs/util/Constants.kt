package com.mivas.myukulelesongs.util

import android.graphics.Color

object Constants {

    const val URL_PRIVACY_POLICY = "https://www.freeprivacypolicy.com/privacy/view/75eb2f2494a017039da6c2b979dbc8b8"
    const val API_KEY_UKULELE_CHORDS = "bbec19965dd6b66598365c7129784401"

    /* Preferences */
    const val PREFS = "templates_prefs"
    const val PREF_FIRST_RUN = "pref_first_run"
    const val PREF_TAB_TEXT_SIZE = "pref_tab_text_size"
    const val PREF_TAB_TEXT_COLOR = "pref_tab_text_color"
    const val PREF_TAB_CHORD_COLOR = "pref_tab_chord_color"
    const val PREF_TAB_BACKGROUND_COLOR = "pref_tab_background_color"
    const val PREF_PREFER_SHARP = "pref_prefer_sharp"

    /* Defaults */
    const val DEFAULT_TAB_TEXT_SIZE = 15
    val DEFAULT_TAB_TEXT_COLOR = Color.parseColor("#333333")
    val DEFAULT_TAB_CHORD_COLOR = Color.parseColor("#e00000")
    val DEFAULT_TAB_BACKGROUND_COLOR = Color.parseColor("#ededed")
    const val DEFAULT_PREFER_SHARP = false

    /* Extra */
    const val EXTRA_ID = "com.mivas.myukulelesongs.EXTRA_ID"
    const val EXTRA_CHORD = "com.mivas.myukulelesongs.EXTRA_CHORD"

    /* Broadcast */
    const val BROADCAST_CUSTOMIZATIONS_UPDATED = "broadcast_customizations_update"

}