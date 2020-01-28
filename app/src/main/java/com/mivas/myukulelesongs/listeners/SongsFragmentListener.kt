package com.mivas.myukulelesongs.listeners

import com.mivas.myukulelesongs.database.model.Song

/**
 * Listener for interactions with a song in [com.mivas.myukulelesongs.ui.fragment.SongsFragment].
 */
interface SongsFragmentListener {

    /**
     * Triggered when a song is clicked.
     */
    fun onSongClicked(song: Song)

    /**
     * Triggered when the edit option of a song is clicked.
     */
    fun onSongEditClicked(song: Song)

    /**
     * Triggered when the delete option of a song is clicked.
     */
    fun onSongDeleteClicked(song: Song)
}