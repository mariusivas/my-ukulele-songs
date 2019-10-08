package com.mivas.myukulelesongs.repository

import android.app.Application
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song
import org.jetbrains.anko.doAsync

class AddEditSongRepository(application: Application) {

    private val songDao = Db.instance.getSongsDao()

    fun insert(song: Song) = doAsync { songDao.insert(song) }
    fun update(song: Song) = doAsync { songDao.update(song) }
    fun delete(song: Song) = doAsync { songDao.delete(song) }
    fun getSongById(id: Long) = songDao.getByIdLive(id)

}