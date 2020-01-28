package com.mivas.myukulelesongs.util

import com.mivas.myukulelesongs.database.model.Song

class FirstRunUtils {

    fun getSampleChordsSong(): Song {
        return Song().apply {
            title = "Happy Birthday Sample Chords"
            type = 0
            strummingPatterns = "D D D D"
            originalKey = "C"
            version = 0
            uniqueId = UniqueIdGenerator.generate()
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

    fun getSampleTabSong(): Song {
        return Song().apply {
            title = "Happy Birthday Sample Tab"
            type = 3
            originalKey = "C"
            version = 0
            uniqueId = UniqueIdGenerator.generate()
            tab =
                """-------------|-------------|-----3-0-------|-1-1-0-------|
---------1-0-|---------3-1-|---------1-0---|-------1-3-1-|
-0-0-2-0-----|-0-0-2-0-----|-0-0---------2-|-------------|
-------------|-------------|---------------|-------------|"""
        }
    }
}