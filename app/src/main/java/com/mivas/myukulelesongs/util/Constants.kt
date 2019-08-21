package com.mivas.myukulelesongs.util

import android.graphics.Color

object Constants {

    /* Preferences */
    const val PREFS = "templates_prefs"
    const val PREF_FIRST_RUN = "pref_first_run"
    const val PREF_TAB_TEXT_SIZE = "pref_tab_text_size"
    const val PREF_TAB_TEXT_COLOR = "pref_tab_text_color"
    const val PREF_TAB_CHORD_COLOR = "pref_tab_chord_color"
    const val PREF_TAB_BACKGROUND_COLOR = "pref_tab_background_color"

    const val DEFAULT_TAB_TEXT_SIZE = 15f
    val DEFAULT_TAB_TEXT_COLOR = Color.parseColor("#333333")
    val DEFAULT_TAB_CHORD_COLOR = Color.parseColor("#e00000")
    val DEFAULT_TAB_BACKGROUND_COLOR = Color.parseColor("#ededed")

    /* URL */
    const val URL_BASE = "https://jsonplaceholder.typicode.com/"
    const val URL_GET_POST = "posts/{id}"
    const val URL_GET_POSTS = "posts"
    const val URL_POST_POST = "posts"
    const val URL_PUT_POST = "posts/{id}"
    const val URL_DELETE_POST = "posts/{id}"

    /* Extra */
    const val EXTRA_ID = "com.mivas.myukulelesongs.EXTRA_ID"

    /* Broadcast */
    const val BROADCAST_CARDS_UPDATED = "broadcast_cards_update"

}