package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song

class SongsRepository {

    private val songDao = Db.instance.getSongsDao()

    fun update(song: Song) =  songDao.update(song)
    fun delete(song: Song) =  songDao.delete(song)
    fun getWithFilter(filter: String) = songDao.getWithFilterLive("%$filter%")

}