package com.mivas.myukulelesongs.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mivas.myukulelesongs.App
import com.mivas.myukulelesongs.database.dao.SongDao
import com.mivas.myukulelesongs.database.model.Song


@Database(entities = [Song::class], version = 1)
abstract class Db : RoomDatabase() {

    abstract fun getSongsDao(): SongDao

    companion object {
        val instance: Db = Room.databaseBuilder(App.instance.applicationContext, Db::class.java, "MUS_DB")
                //.allowMainThreadQueries()
                .build()
    }

}