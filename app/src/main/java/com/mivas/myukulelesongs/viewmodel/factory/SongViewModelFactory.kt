package com.mivas.myukulelesongs.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mivas.myukulelesongs.viewmodel.TabViewModel


class SongViewModelFactory(private val songId: Long) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabViewModel(songId) as T
    }
}