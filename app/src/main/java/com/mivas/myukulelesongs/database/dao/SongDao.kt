package com.mivas.myukulelesongs.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.mivas.myukulelesongs.database.model.Song


@Dao
interface SongDao {

    @Insert(onConflict = REPLACE)
    fun insert(song: Song)

    @Insert(onConflict = REPLACE)
    fun insertAll(songs: List<Song>)

    @Update
    fun update(song: Song)

    @Update
    fun updateAll(songs: List<Song>)

    @Query("SELECT * FROM songs where id = :id")
    fun getByIdLive(id: Long): LiveData<Song>

    @Query("SELECT * FROM songs ORDER BY title")
    fun getAllLive(): LiveData<List<Song>>

    @Query("SELECT * FROM songs ORDER BY title")
    fun getAll(): List<Song>

    @Query("SELECT * FROM songs where unique_id = ''")
    fun getNoUniqueIds(): List<Song>

    @Query("SELECT * FROM songs WHERE deleted = 1")
    fun getDeleted(): List<Song>

    @Query("SELECT * FROM songs WHERE uploaded = 0")
    fun getNotUploaded(): List<Song>

    @Query("SELECT * FROM songs WHERE (title LIKE :query OR author LIKE :query) AND deleted = 0 ORDER BY title")
    fun getWithFilterLive(query: String): LiveData<List<Song>>

    @Delete
    fun delete(song: Song): Int

    @Delete
    fun deleteAll(songs: List<Song>): Int

    @Query("DELETE FROM songs")
    fun deleteAll()

    @Query("DELETE FROM songs where id = :id")
    fun deleteById(id: Long)
}