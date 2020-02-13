package com.mivas.myukulelesongs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.drive.DriveHelper
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.util.*
import kotlinx.android.synthetic.main.activity_loading.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import java.util.*

/**
 * First activity of the app. Handles loading of data from Google Drive it this is enabled.
 */
class LoadingActivity : AppCompatActivity(R.layout.activity_loading) {

    companion object {
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        populateUniqueIds()
        checkFirstRun()
        driveSignIn()
    }

    /**
     * Views initializer.
     */
    private fun initViews() {
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }
        syncImage.startAnimation(rotate)
    }

    /**
     * Signs in to Google Drive.
     */
    private fun driveSignIn() {
        if (Prefs.getBoolean(Constants.PREF_DRIVE_SYNC) && NetworkUtils.isInternetAvailable()) {
            startActivityForResult(GoogleHelper.getSignInClient(this).signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN && data != null) {
            handleSignIn(data)
        }
    }

    /**
     * Handles data received from Google sign in.
     */
    private fun handleSignIn(data: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener {
            val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_APPDATA)).apply { selectedAccount = it.account }
            DriveHelper.drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName(getString(R.string.app_name))
                .build()
            startDriveSync()
        }.addOnFailureListener {
            toast(R.string.settings_activity_toast_login_failed)
            startActivity(Intent(this@LoadingActivity, MainActivity::class.java))
            finish()
        }
    }

    /**
     * Starts songs synchronization with Google Drive.
     */
    private fun startDriveSync() {
        val afterRestore = intent.hasExtra(Constants.EXTRA_AFTER_RESTORE) && intent.getBooleanExtra(Constants.EXTRA_AFTER_RESTORE, false)
        lifecycleScope.launch(IO) {
            DriveSync.syncAll(this, afterRestore)
            withContext(Main) {
                startActivity(Intent(this@LoadingActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    /**
     * Generate unique ids for songs that don't have one.
     */
    private fun populateUniqueIds() = lifecycleScope.launch(IO) {
        // TODO Legacy. Remove in 2021 or later
        val dao = Db.instance.getSongsDao()
        val songs = dao.getNoUniqueIds()
        if (songs.isNotEmpty()) {
            songs.forEach { it.uniqueId = UniqueIdGenerator.generate() }
            dao.updateAll(songs)
        }
    }

    /**
     * Checks if this is the first time running the app.
     */
    private fun checkFirstRun() {
        if (Prefs.getBoolean(Constants.PREF_FIRST_RUN, true)) {
            Prefs.putBoolean(Constants.PREF_FIRST_RUN, false)
            val dao = Db.instance.getSongsDao()
            val sampleChordsSong = FirstRunUtils().getSampleChordsSong()
            val sampleTabSong = FirstRunUtils().getSampleTabSong()
            lifecycleScope.launch(IO) {
                dao.insert(sampleChordsSong)
                dao.insert(sampleTabSong)
            }
        }
    }

}