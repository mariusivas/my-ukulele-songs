package com.mivas.myukulelesongs

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Base [Application] that provides access to its instance.
 */
class App : Application() {

    companion object {
        lateinit var instance: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseAnalytics.getInstance(this)
    }

}