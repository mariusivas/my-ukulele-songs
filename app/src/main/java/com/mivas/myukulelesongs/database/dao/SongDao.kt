package com.mivas.myukulelesongs.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.mivas.myukulelesongs.database.model.Song

/**
 * DAO for a [Song]
 */
@Dao
interface SongDao {

    /**
     * Inserts a song into db.
     */
    @Insert(onConflict = REPLACE)
    fun insert(song: Song)

    /**
     * Inserts multiple songs into db.
     */
    @Insert(onConflict = REPLACE)
    fun insertAll(songs: List<Song>)

    /**
     * Updates a song into db.
     */
    @Update
    fun update(song: Song)

    /**
     * Updates multiple songs into db.
     */
    @Update
    fun updateAll(songs: List<Song>)

    /**
     * Retrieves a LiveData of a song from the db.
     *
     * @param id The id of the song.
     */
    @Query("SELECT * FROM songs where id = :id")
    fun getByIdLive(id: Long): LiveData<Song>

    /**
     * Retrieves all the songs from db.
     */
    @Query("SELECT * FROM songs ORDER BY title")
    fun getAll(): List<Song>

    /**
     * Retrieves the songs from the db that don't have a unique id.
     */
    @Query("SELECT * FROM songs where unique_id = ''")
    fun getNoUniqueIds(): List<Song>

    /**
     * Retrieves the songs from the db that are marked as deleted.
     */
    @Query("SELECT * FROM songs WHERE deleted = 1")
    fun getDeleted(): List<Song>

    /**
     * Retrieves the songs from the db that are marked as not uploaded to Drive.
     */
    @Query("SELECT * FROM songs WHERE uploaded = 0")
    fun getNotUploaded(): List<Song>

    /**
     * Retrieves a LiveData of all the songs using a filter.
     */
    @Query("SELECT * FROM songs WHERE (title LIKE :query OR author LIKE :query) AND deleted = 0 ORDER BY title")
    fun getWithFilterLive(query: String): LiveData<List<Song>>

    /**
     * Deletes a song from the db.
     */
    @Delete
    fun delete(song: Song): Int

    /**
     * Deletes multiple songs from the db.
     */
    @Delete
    fun deleteAll(songs: List<Song>): Int

    /**
     * Deletes all the songs from the db.
     */
    @Query("DELETE FROM songs")
    fun deleteAll()
}