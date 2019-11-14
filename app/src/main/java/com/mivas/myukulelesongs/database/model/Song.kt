package com.mivas.myukulelesongs.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "title") var title: String,
                @ColumnInfo(name = "author") var author: String,
                @ColumnInfo(name = "type") var type: Int,
                @ColumnInfo(name = "strumming_patterns") var strummingPatterns: String,
                @ColumnInfo(name = "picking_patterns") var pickingPatterns: String,
                @ColumnInfo(name = "original_key") var originalKey: String,
                @ColumnInfo(name = "tab") var tab: String) {
    constructor() : this(null, "", "", 0, "", "", "", "")
    constructor(title: String = "", author: String = "", type: Int = 0, strummingPatterns: String = "", pickingPatterns: String = "", originalKey: String = "", tab: String = "") : this(null, title, author, type, strummingPatterns, pickingPatterns, originalKey, tab)
}
