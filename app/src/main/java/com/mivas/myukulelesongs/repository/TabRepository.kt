package com.mivas.myukulelesongs.repository

import android.app.Application
import com.mivas.myukulelesongs.database.Db

class TabRepository(application: Application) {

    private val songDao = Db.instance.getSongsDao()

    fun getSongById(id: Long) = songDao.getByIdLive(id)

}