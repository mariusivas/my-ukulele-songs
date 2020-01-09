package com.mivas.myukulelesongs.drive.model

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("songs") var songs: MutableList<DriveSong>
)