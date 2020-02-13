package com.mivas.myukulelesongs.viewmodel

import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.*
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.repository.SongsRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
 * ViewModel for [com.mivas.myukulelesongs.ui.fragment.SongsFragment].
 */
class SongsViewModel : ViewModel() {

    private var songsRepository = SongsRepository()

    var searchMode = MutableLiveData<Boolean>().apply { value = false }
    val filter = MutableLiveData<String>().apply { value = "" }
    var songs = Transformations.switchMap(filter, ::getWithFilter)
    val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (searchMode.value!!) {
                toggleSearchMode()
            }
        }
    }.apply { isEnabled = false }

    /**
     * Updates a song in db.
     */
    fun updateSong(song: Song) = viewModelScope.launch(IO) { songsRepository.update(song) }

    /**
     * Deletes a song from db.
     */
    fun deleteSong(song: Song) = viewModelScope.launch(IO) { songsRepository.delete(song) }

    /**
     * Returns a random song.
     */
    fun getRandomSong() = songs.value!!.random()

    /**
     * Toggles the search mode.
     */
    fun toggleSearchMode() {
        searchMode.value = !searchMode.value!!
        backPressedCallback.isEnabled = !backPressedCallback.isEnabled
    }

    /**
     * Returns a filtered list of songs.
     *
     * @param filter The filter
     * @return A filtered list of songs
     */
    private fun getWithFilter(filter: String) = songsRepository.getWithFilter(filter)

}