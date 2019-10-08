package com.mivas.myukulelesongs.model

import com.google.gson.annotations.SerializedName

class ExportSongsJson(@SerializedName("songs") val exportedSongs: List<ExportedSong>) {

}