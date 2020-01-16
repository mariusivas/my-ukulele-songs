package com.mivas.myukulelesongs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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


class LoadingActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        initViews()
        populateUniqueIds()
        checkFirstRun()
        driveSignIn()
    }

    private fun initViews() {
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }
        syncImage.startAnimation(rotate)
    }

    private fun driveSignIn() {
        if (Prefs.getBoolean(Constants.PREF_DRIVE_SYNC)) {
            startActivityForResult(GoogleUtils.getSignInClient(this).signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
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

    private fun populateUniqueIds() = lifecycleScope.launch(IO) {
        // TODO Legacy. Remove in 2021 or later
        val dao = Db.instance.getSongsDao()
        val songs = dao.getNoUniqueIds()
        if (songs.isNotEmpty()) {
            songs.forEach { it.uniqueId = IdUtils.generateUniqueId() }
            dao.updateAll(songs)
        }
    }

    private fun checkFirstRun() {
        if (Prefs.getBoolean(Constants.PREF_FIRST_RUN, true)) {
            Prefs.putBoolean(Constants.PREF_FIRST_RUN, false)
            val sampleSong = FirstRunUtils().getSampleSong()
            lifecycleScope.launch(IO) {
                val dao = Db.instance.getSongsDao()
                dao.insert(sampleSong)
            }
        }
    }

}