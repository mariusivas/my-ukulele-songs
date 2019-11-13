package com.mivas.myukulelesongs.repository

import android.app.Application
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song
import org.jetbrains.anko.doAsync

class TabRepository(application: Application) {

    private val songDao = Db.instance.getSongsDao()

    fun getSongById(id: Long) = songDao.getByIdLive(id)
    fun update(song: Song) = doAsync { songDao.update(song) }

}