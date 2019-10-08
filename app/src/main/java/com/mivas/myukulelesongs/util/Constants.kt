package com.mivas.myukulelesongs.util

import android.graphics.Color

object Constants {

    const val URL_PRIVACY_POLICY = "https://www.freeprivacypolicy.com/privacy/view/75eb2f2494a017039da6c2b979dbc8b8"

    /* Preferences */
    const val PREFS = "templates_prefs"
    const val PREF_FIRST_RUN = "pref_first_run"
    const val PREF_TAB_TEXT_SIZE = "pref_tab_text_size"
    const val PREF_TAB_TEXT_COLOR = "pref_tab_text_color"
    const val PREF_TAB_CHORD_COLOR = "pref_tab_chord_color"
    const val PREF_TAB_BACKGROUND_COLOR = "pref_tab_background_color"

    /* Defaults */
    const val DEFAULT_TAB_TEXT_SIZE = 15
    val DEFAULT_TAB_TEXT_COLOR = Color.parseColor("#333333")
    val DEFAULT_TAB_CHORD_COLOR = Color.parseColor("#e00000")
    val DEFAULT_TAB_BACKGROUND_COLOR = Color.parseColor("#ededed")

    /* Extra */
    const val EXTRA_ID = "com.mivas.myukulelesongs.EXTRA_ID"

    /* Broadcast */
    const val BROADCAST_CUSTOMIZATIONS_UPDATED = "broadcast_customizations_update"

}