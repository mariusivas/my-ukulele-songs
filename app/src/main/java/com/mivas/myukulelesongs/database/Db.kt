package com.mivas.myukulelesongs.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mivas.myukulelesongs.App
import com.mivas.myukulelesongs.database.dao.SongDao
import com.mivas.myukulelesongs.database.model.Song


@Database(entities = [Song::class], version = 3, exportSchema = false)
abstract class Db : RoomDatabase() {

    abstract fun getSongsDao(): SongDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE songs ADD COLUMN type INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE songs ADD COLUMN picking_patterns TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE songs ADD COLUMN original_key TEXT NOT NULL DEFAULT ''")
            }
        }

        val instance: Db = Room.databaseBuilder(App.instance.applicationContext, Db::class.java, "MUS_DB")
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
    }

}