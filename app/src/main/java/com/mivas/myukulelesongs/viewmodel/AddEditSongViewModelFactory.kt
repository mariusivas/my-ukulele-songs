package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider


class AddEditSongViewModelFactory(private val application: Application, private val songId: Long) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditSongViewModel(application, songId) as T
    }
}