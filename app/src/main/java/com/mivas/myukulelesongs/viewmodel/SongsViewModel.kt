package com.mivas.myukulelesongs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.exception.NoSongsException
import com.mivas.myukulelesongs.repository.SongsRepository
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.FirstRunUtils
import com.mivas.myukulelesongs.util.Prefs
import kotlin.random.Random

class SongsViewModel(application: Application) : AndroidViewModel(application) {

    private var songsRepository = SongsRepository(application)
    private var songs = songsRepository.getAll()

    fun insertSong(song: Song) = songsRepository.insert(song)
    fun updateSong(song: Song) = songsRepository.update(song)
    fun deleteSong(song: Song) = songsRepository.delete(song)
    fun deleteAllSongs() = songsRepository.deleteAll()
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
}