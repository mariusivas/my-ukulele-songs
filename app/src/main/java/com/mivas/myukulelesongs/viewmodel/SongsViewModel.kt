package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.exception.NoSongsException
import com.mivas.myukulelesongs.repository.SongsRepository
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.FirstRunUtils
import com.mivas.myukulelesongs.util.Prefs
import kotlin.random.Random

class SongsViewModel(application: Application) : AndroidViewModel(application) {

    private var songsRepository = SongsRepository(application)
    val filter = MutableLiveData<String>().apply { postValue("") }
    private var songs = Transformations.switchMap(filter, ::getWithFilter)

    fun insertSong(song: Song) = songsRepository.insert(song)
    fun deleteSong(song: Song) = songsRepository.delete(song)
    fun getAllSongs() = songs

    fun checkFirstRun() {
        if (Prefs.getBoolean(Constants.PREF_FIRST_RUN, true)) {
            Prefs.putBoolean(Constants.PREF_FIRST_RUN, false)
            insertSong(FirstRunUtils().getSampleSong())
        }
    }

    fun getRandomSong(): Song {
        val allSongs = songs.value!!
        if (allSongs.isEmpty()) throw NoSongsException()
        return allSongs[Random.nextInt(allSongs.size)]
    }

    private fun getWithFilter(filter: String) = songsRepository.getWithFilter(filter)

}