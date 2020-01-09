package com.mivas.myukulelesongs.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mivas.myukulelesongs.viewmodel.AddEditSongViewModel


class AddEditSongViewModelFactory(private val songId: Long) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditSongViewModel(songId) as T
    }
}