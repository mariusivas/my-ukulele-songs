package com.mivas.myukulelesongs.model

import com.mivas.myukulelesongs.database.model.Song

data class ExportedSong(var title: String,
                        var author: String,
                        var type: Int,
                        var strummingPatterns: String,
                        var pickingPatterns: String,
                        var originalKey: String,
                        var tab: String) {
    constructor() : this("", "", 0, "", "", "", "")
    constructor(song: Song) : this(song.title, song.author, song.type, song.strummingPatterns, song.pickingPatterns, song.originalKey, song.tab)

    fun toSong() = Song(title, author, type, strummingPatterns, pickingPatterns, originalKey, tab)
}
