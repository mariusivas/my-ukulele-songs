package com.mivas.myukulelesongs.model

import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.util.UniqueIdGenerator

/**
 * Model representing a song that is exported to a file.
 *
 * @param title Song title
 * @param author Song author
 * @param type Song type. Can be 0 - strumming, 1 - picking, 2 - strumming & picking, 3 - tab
 * @param strummingPatterns Song strummingPatterns
 * @param pickingPatterns Song pickingPatterns
 * @param originalKey Song originalKey
 * @param tab Song text
 *
 * @constructor Create an [ExportedSong] from a [Song]
 */
data class ExportedSong(var title: String,
                        var author: String,
                        var type: Int,
                        var strummingPatterns: String,
                        var pickingPatterns: String,
                        var originalKey: String,
                        var tab: String) {
    constructor(song: Song) : this(song.title, song.author, song.type, song.strummingPatterns, song.pickingPatterns, song.originalKey, song.tab)

    /**
     * Converts the [ExportedSong] to a [Song]
     */
    fun toSong() = Song(title, author, type, strummingPatterns, pickingPatterns, originalKey, tab, 0, false, false, UniqueIdGenerator.generate())
}
