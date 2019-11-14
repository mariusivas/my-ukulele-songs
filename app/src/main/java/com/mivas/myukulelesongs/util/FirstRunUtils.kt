package com.mivas.myukulelesongs.util

import com.mivas.myukulelesongs.database.model.Song

class FirstRunUtils {

    fun getSampleSong(): Song {
        return Song().apply {
            title = "Happy Birthday Sample Song"
            type = 0
            strummingPatterns = "D D D D"
            originalKey = "C"
            tab =
"""      C           G7
Happy birthday to you
      G7          C
Happy birthday to you
      C             F
Happy birthday dear user
      C        G7 C
Happy birthday to you"""
        }
    }
}