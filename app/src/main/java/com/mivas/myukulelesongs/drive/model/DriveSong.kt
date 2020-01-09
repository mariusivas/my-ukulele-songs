package com.mivas.myukulelesongs.drive.model

import com.google.gson.annotations.SerializedName
import com.mivas.myukulelesongs.database.model.Song

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

    fun toSong() = Song(title, author, type, strummingPatterns, pickingPatterns, originalKey, tab, version, true, false, uniqueId)
}