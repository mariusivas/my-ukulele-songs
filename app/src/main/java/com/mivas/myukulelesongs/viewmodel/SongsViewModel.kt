package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.SongsRepository

class SongsViewModel(application: Application) : AndroidViewModel(application) {

    private var songsRepository = SongsRepository(application)
    private var songs = songsRepository.getAll()

    fun insertSong(song: Song) = songsRepository.insert(song)
    fun updateSong(song: Song) = songsRepository.update(song)
    fun deleteSong(song: Song) = songsRepository.delete(song)
    fun deleteAllSongs() = songsRepository.deleteAll()
    fun getAllSongs() = songs
}