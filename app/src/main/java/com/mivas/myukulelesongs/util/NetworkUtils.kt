package com.mivas.myukulelesongs.util

import java.lang.Exception

/**
 * Util class that handles network connection.
 */
object NetworkUtils {

    /**
     * Checks if the use is connected to the internet.
     *
     * @return True if connected to internet, else false
     */
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