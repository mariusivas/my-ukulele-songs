package com.mivas.myukulelesongs.listeners

import com.mivas.myukulelesongs.database.model.Song

interface SongsFragmentListener {

    fun onSongClicked(song: Song)
    fun onSongEditClicked(song: Song)
    fun onSongDeleteClicked(song: Song)
}