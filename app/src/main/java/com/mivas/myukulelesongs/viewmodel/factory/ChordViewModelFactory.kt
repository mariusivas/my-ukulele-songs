package com.mivas.myukulelesongs.viewmodel.factory

import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.mivas.myukulelesongs.viewmodel.ChordViewModel


class ChordViewModelFactory(private val chord: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChordViewModel(chord) as T
    }
}