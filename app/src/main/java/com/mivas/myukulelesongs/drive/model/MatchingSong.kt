package com.mivas.myukulelesongs.drive.model

import com.mivas.myukulelesongs.database.model.Song

/**
 * Model representing 2 songs, one on the local device and the other one on Google drive, that have the same unique id.
 *
 * @param local The song on the local device
 * @param cloud The song on Google Drive
 */
data class MatchingSong(
    val local: Song,
    val cloud: DriveSong
)