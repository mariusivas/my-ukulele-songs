package com.mivas.myukulelesongs.model

import com.google.gson.annotations.SerializedName

/**
 * JSON containing a list of [ExportedSong] that is to be stored in a file.
 *
 * @param exportedSongs The list of [ExportedSong]
 */
class ExportSongsJson(@SerializedName("songs") val exportedSongs: List<ExportedSong>) {

}