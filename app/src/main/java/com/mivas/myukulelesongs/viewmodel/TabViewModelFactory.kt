package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider


class TabViewModelFactory(private val application: Application, private val songId: Long) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabViewModel(application, songId) as T
    }
}