package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.*
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.SongsRepository
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.FirstRunUtils
import com.mivas.myukulelesongs.util.Prefs
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class SongsViewModel : ViewModel() {

    private var songsRepository = SongsRepository()

    var searchMode = MutableLiveData<Boolean>().apply { value = false }
    val filter = MutableLiveData<String>().apply { value = "" }
    var songs = Transformations.switchMap(filter, ::getWithFilter)

    fun updateSong(song: Song) = viewModelScope.launch(IO) { songsRepository.update(song) }

    fun deleteSong(song: Song) = viewModelScope.launch(IO) { songsRepository.delete(song) }

    fun checkFirstRun() {
        if (Prefs.getBoolean(Constants.PREF_FIRST_RUN, true)) {
            Prefs.putBoolean(Constants.PREF_FIRST_RUN, false)
            val sampleSong = FirstRunUtils().getSampleSong()
            viewModelScope.launch(IO) { songsRepository.insert(sampleSong) }
        }
    }

    fun getRandomSong() = songs.value!!.random()

    fun revertSearchMode() {
        searchMode.value = !searchMode.value!!
    }

    private fun getWithFilter(filter: String) = songsRepository.getWithFilter(filter)

}