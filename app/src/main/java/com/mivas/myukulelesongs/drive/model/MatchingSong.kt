package com.mivas.myukulelesongs.drive.model

import com.mivas.myukulelesongs.database.model.Song

data class MatchingSong(
    val local: Song,
    val cloud: DriveSong
)