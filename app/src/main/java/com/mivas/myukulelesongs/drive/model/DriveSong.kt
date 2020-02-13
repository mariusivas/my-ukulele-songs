package com.mivas.myukulelesongs.drive.model

import com.google.gson.annotations.SerializedName
import com.mivas.myukulelesongs.database.model.Song

/**
 * Model representing a [Song] stored on Google Drive.
 *
 * @param title The title of the song. Should be a simple string
 * @param author The author of the song. Should be a simple string
 * @param type The type of the song. Can be 0 - strumming, 1 - picking, 2 - strumming & picking
 * @param strummingPatterns The strumming patterns of the song. A string that can have multiple lines
 * @param pickingPatterns The picking patterns of the song. A string that can have multiple lines
 * @param originalKey The original key of the song. Should be a simple string
 * @param tab The text of the song. Usually a huge string that can have multiple lines
 * @param version The version of the song. This is usually incremented when a song is updated
 * @param uniqueId Unique id of the song. This a 16 character string generated at song creation
 */
data class DriveSong(
    @SerializedName("title") var title: String,
    @SerializedName("author") var author: String,
    @SerializedName("type") var type: Int,
    @SerializedName("strumming_patterns") var strummingPatterns: String,
    @SerializedName("picking_patterns") var pickingPatterns: String,
    @SerializedName("original_key") var originalKey: String,
    @SerializedName("tab") var tab: String,
    @SerializedName("version") var version: Long,
    @SerializedName("unique_id") var uniqueId: String
) {
    constructor() : this("", "", 0, "", "", "", "", 0, "")
    constructor(song: Song) : this(song.title, song.author, song.type, song.strummingPatterns, song.pickingPatterns, song.originalKey, song.tab, 0, "")

    /**
     * Converts the [DriveSong] to a [Song]
     */
    fun toSong() = Song(title, author, type, strummingPatterns, pickingPatterns, originalKey, tab, version, true, false, uniqueId)
}