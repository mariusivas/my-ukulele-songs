package com.mivas.myukulelesongs.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "title") var title: String,
                @ColumnInfo(name = "author") var author: String,
                @ColumnInfo(name = "strumming_patterns") var strummingPatterns: String,
                @ColumnInfo(name = "tab") var tab: String) {
    constructor() : this(null, "", "", "", "")
    constructor(title: String = "", author: String = "", strummingPatterns: String = "", tab: String = "") : this(null, title, author, strummingPatterns, tab)
}
