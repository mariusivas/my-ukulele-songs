package com.mivas.myukulelesongs.viewmodel

import androidx.lifecycle.*
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs

class SettingsViewModel : ViewModel() {

    fun setPreferSharp(sharp: Boolean) = Prefs.putBoolean(Constants.PREF_PREFER_SHARP, sharp)
    fun getPreferSharp() = Prefs.getBoolean(Constants.PREF_PREFER_SHARP, Constants.DEFAULT_PREFER_SHARP)
}