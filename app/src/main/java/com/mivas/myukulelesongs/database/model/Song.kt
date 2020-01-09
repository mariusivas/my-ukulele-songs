package com.mivas.myukulelesongs.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mivas.myukulelesongs.drive.model.DriveSong
import com.mivas.myukulelesongs.util.IdUtils

@Entity(tableName = "songs")
data class Song(@PrimaryKey(autoGenerate = true) var id: Long?,
                @ColumnInfo(name = "title") var title: String,
                @ColumnInfo(name = "author") var author: String,
                @ColumnInfo(name = "type") var type: Int,
                @ColumnInfo(name = "strumming_patterns") var strummingPatterns: String,
                @ColumnInfo(name = "picking_patterns") var pickingPatterns: String,
                @ColumnInfo(name = "original_key") var originalKey: String,
                @ColumnInfo(name = "tab") var tab: String,
                @ColumnInfo(name = "version") var version: Long,
                @ColumnInfo(name = "uploaded") var uploaded: Boolean,
                @ColumnInfo(name = "deleted") var deleted: Boolean,
                @ColumnInfo(name = "unique_id") var uniqueId: String) {
    constructor() : this(null, "", "", 0, "", "", "", "", 0, false, false, IdUtils.generateUniqueId())
    constructor(title: String = "", author: String = "", type: Int = 0, strummingPatterns: String = "", pickingPatterns: String = "", originalKey: String = "", tab: String = "", version: Long = 0, uploaded: Boolean = false, deleted: Boolean = false, uniqueId: String = IdUtils.generateUniqueId()) : this(null, title, author, type, strummingPatterns, pickingPatterns, originalKey, tab, version, false, false, uniqueId)

    fun toDriveSong() = DriveSong(title, author, type, strummingPatterns, pickingPatterns, originalKey, tab, version, uniqueId)
}