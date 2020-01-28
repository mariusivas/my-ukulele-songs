package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song

/**
 * Repository for [com.mivas.myukulelesongs.viewmodel.SongsViewModel].
 */
class SongsRepository {

    private val songDao = Db.instance.getSongsDao()

    /**
     * Updates a song into db.
     */
    fun update(song: Song) =  songDao.update(song)

    /**
     * Deletes a song from db.
     */
    fun delete(song: Song) =  songDao.delete(song)

    /**
     * Retrieves a LiveData of a song from the db for the given filter.
     */
    fun getWithFilter(filter: String) = songDao.getWithFilterLive("%$filter%")

}