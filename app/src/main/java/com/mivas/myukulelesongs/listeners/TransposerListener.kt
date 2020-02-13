package com.mivas.myukulelesongs.listeners

/**
 * Listener for when a song is transposed.
 */
interface TransposerListener {

    /**
     * Triggered after a text was transposed.
     *
     * @param transposedText The new transposed text
     * @param final True if the transposition if final and the song should be updated, else false
     */
    fun onTextTransposed(transposedText: String, final: Boolean = false)
}