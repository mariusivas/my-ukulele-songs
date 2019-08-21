package com.mivas.myukulelesongs.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.mivas.myukulelesongs.database.model.Song


@Dao
interface SongDao {

    @Insert(onConflict = REPLACE)
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Query("SELECT * FROM songs where id = :id")
    fun getById(id: Long): LiveData<Song>

    @Query("SELECT * FROM songs ORDER BY title")
    fun getAll(): LiveData<List<Song>>

    @Delete
    fun delete(song: Song)

    @Query("DELETE FROM songs")
    fun deleteAll()

    @Query("DELETE FROM songs where id = :id")
    fun deleteById(id: Long)
}