package com.mivas.myukulelesongs.viewmodel.factory

import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.mivas.myukulelesongs.viewmodel.TabViewModel


class TabViewModelFactory(private val application: Application, private val songId: Long) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TabViewModel(application, songId) as T
    }
}