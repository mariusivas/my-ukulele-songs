package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.AddEditSongRepository

class AddEditSongViewModel(application: Application, songId: Long) : AndroidViewModel(application) {

    private var addEditSongRepository = AddEditSongRepository(application)
    private var song = addEditSongRepository.getSongById(songId)
    var selectedType: MutableLiveData<Int> = MutableLiveData()

    init {
        selectedType.value = 0
    }

    fun insertSong(song: Song) = addEditSongRepository.insert(song)
    fun updateSong(song: Song) = addEditSongRepository.update(song)
    fun deleteSong(song: Song) = addEditSongRepository.delete(song)
    fun getSong() = song
}