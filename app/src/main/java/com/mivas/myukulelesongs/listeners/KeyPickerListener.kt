package com.mivas.myukulelesongs.listeners

/**
 * Listener for when a key is selected from [com.mivas.myukulelesongs.ui.dialog.KeyPickerDialog].
 */
interface KeyPickerListener {

    /**
     * Triggered when a key iss selected.
     *
     * @param key The selected key
     */
    fun onKeyClicked(key: String)
}