package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song

/**
 * Repository for [com.mivas.myukulelesongs.viewmodel.TabViewModel].
 */
class TabRepository {

    private val songDao = Db.instance.getSongsDao()

    /**
     * Updates a song into db.
     */
    fun update(song: Song) = songDao.update(song)

    /**
     * Retrieves a LiveData of a song from db.
     */
    fun getSongById(id: Long) = songDao.getByIdLive(id)

}