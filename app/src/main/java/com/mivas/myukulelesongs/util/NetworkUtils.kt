package com.mivas.myukulelesongs.util

import java.lang.Exception


object NetworkUtils {

    fun isInternetAvailable(): Boolean {
        try {
            val ipProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: Exception) {
        }
        return false
    }
}