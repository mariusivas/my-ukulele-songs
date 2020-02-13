package com.mivas.myukulelesongs.drive.model

import com.google.gson.annotations.SerializedName

/**
 * Model of config file that is stored on Google Drive. This acts as an online db.
 *
 * @param songs The list of [DriveSong]
 */
data class Config(
    @SerializedName("songs") var songs: MutableList<DriveSong>
)