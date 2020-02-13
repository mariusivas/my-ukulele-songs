package com.mivas.myukulelesongs.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

/**
 * Helper class that handles Google Authentication.
 */
object GoogleHelper {

    /**
     * Checks if is currently signed in too Google.
     *
     * @param context The context
     * @return True if signed in, else false
     */
    fun isSignedIn(context: Context): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }

    /**
     * Signs out from Google.
     *
     * @param activity The activity
     */
    fun signOut(activity: Activity) {
        getSignInClient(activity).signOut()
    }

    /**
     * Returns the sign in client.
     *
     * @param activity The activity
     * @return The sign in client
     */
    fun getSignInClient(activity: Activity): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        return GoogleSignIn.getClient(activity, signInOptions)
    }
}