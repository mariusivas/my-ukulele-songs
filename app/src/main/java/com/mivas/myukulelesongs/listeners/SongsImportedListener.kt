package com.mivas.myukulelesongs.listeners

/**
 * Interface for when songs are imported in the app.
 */
interface SongsImportedListener {

    /**
     * Triggered after songs are imported in the app.
     *
     * @param count The number of imported songs
     */
    fun onSongsImported(count: Int)
}