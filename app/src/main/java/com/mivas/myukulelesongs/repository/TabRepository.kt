package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song

class TabRepository {

    private val songDao = Db.instance.getSongsDao()

    fun getSongById(id: Long) = songDao.getByIdLive(id)
    fun update(song: Song) = songDao.update(song)

}