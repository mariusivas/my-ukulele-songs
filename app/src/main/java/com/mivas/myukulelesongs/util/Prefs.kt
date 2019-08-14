package com.mivas.myukulelesongs.util

import android.content.Context
import com.mivas.myukulelesongs.App

object Prefs {

    private val prefs = App.instance.applicationContext.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) = prefs.edit().putString(key, value).commit()
    fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).commit()
    fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).commit()
    fun putFloat(key: String, value: Float) = prefs.edit().putFloat(key, value).commit()
    fun putLong(key: String, value: Long) = prefs.edit().putLong(key, value).commit()

    fun getString(key: String, defValue: String = "") = prefs.getString(key, defValue)!!
    fun getBoolean(key: String, defValue: Boolean = false) = prefs.getBoolean(key, defValue)
    fun getInt(key: String, defValue: Int = 0) = prefs.getInt(key, defValue)
    fun getFloat(key: String, defValue: Float = 0f) = prefs.getFloat(key, defValue)
    fun getLong(key: String, defValue: Long = 0L) = prefs.getLong(key, defValue)


}