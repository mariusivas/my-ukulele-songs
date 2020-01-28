package com.mivas.myukulelesongs.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mivas.myukulelesongs.viewmodel.ChordInfoViewModel


class ChordViewModelFactory(private val chord: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChordInfoViewModel(chord) as T
    }
}