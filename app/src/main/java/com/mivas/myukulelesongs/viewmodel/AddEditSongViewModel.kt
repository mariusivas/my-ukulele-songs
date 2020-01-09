package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.*
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.AddEditSongRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddEditSongViewModel(songId: Long) : ViewModel() {

    private val addEditSongRepository = AddEditSongRepository()

    val song = addEditSongRepository.getSongById(songId)
    var selectedType = MutableLiveData<Int>().apply { value = 0 }
    var isEdit = songId != -1L

    fun insertSong(song: Song) = viewModelScope.launch(IO) { addEditSongRepository.insert(song) }
    fun updateSong(song: Song) = viewModelScope.launch(IO) { addEditSongRepository.update(song) }
    fun deleteSong(song: Song) = viewModelScope.launch(IO) { addEditSongRepository.delete(song) }
}