package com.mivas.myukulelesongs.repository

import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song

/**
 * Repository for [com.mivas.myukulelesongs.viewmodel.AddEditSongViewModel].
 */
class AddEditSongRepository {

    private val songDao = Db.instance.getSongsDao()

    /**
     * Inserts a song into db.
     */
    fun insert(song: Song) = songDao.insert(song)

    /**
     * Updates a song into db.
     */
    fun update(song: Song) = songDao.update(song)

    /**
     * Deletes a song from db.
     */
    fun delete(song: Song) = songDao.delete(song)

    /**
     * Retrieves a LiveData of a song from the db.
     */
    fun getSongById(id: Long) = songDao.getByIdLive(id)

}