package com.mivas.myukulelesongs.ui.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import com.google.gson.Gson
import com.mivas.myukulelesongs.BuildConfig
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.listeners.SongsImportedListener
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.drive.DriveHelper
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.model.ExportSongsJson
import com.mivas.myukulelesongs.util.ExportHelper
import com.mivas.myukulelesongs.util.GoogleHelper
import com.mivas.myukulelesongs.util.Prefs
import com.mivas.myukulelesongs.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*

/**
 * Activity of app settings.
 */
class SettingsActivity : AppCompatActivity(R.layout.activity_settings), SongsImportedListener {

    private lateinit var viewModel: SettingsViewModel

    companion object {
        private const val REQUEST_CODE_IMPORT_SONG = 1
        private const val REQUEST_CODE_RESTORE_SONGS = 2
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initViews()
        initListeners()
    }

    /**
     * ViewModel initializer.
     */
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
    }

    /**
     * Views initializer.
     */
    private fun initViews() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.settings_activity_title)
        }
        driveSyncCheckbox.isChecked = Prefs.getBoolean(Constants.PREF_DRIVE_SYNC)
        preferSharpToogle.isChecked = viewModel.getPreferSharp()
        versionDescriptionText.text = BuildConfig.VERSION_NAME
    }

    /**
     * Listeners initializer.
     */
    private fun initListeners() {
        importLayout.setOnClickListener {
            checkPermissionAndImport(REQUEST_CODE_IMPORT_SONG)
        }
        backupLayout.setOnClickListener {
            lifecycleScope.launch(IO) {
                val songs = Db.instance.getSongsDao().getAll()
                val fileName = "Backup${System.currentTimeMillis()}.mus"
                withContext(Main) { ExportHelper.launchExportMusIntent(this@SettingsActivity, songs, fileName) }
            }
        }
        restoreLayout.setOnClickListener {
            alert(R.string.settings_activity_dialog_restore_songs_description, R.string.settings_activity_dialog_restore_songs_title) {
                negativeButton(R.string.generic_cancel) {}
                positiveButton(R.string.generic_restore) {
                    checkPermissionAndImport(REQUEST_CODE_RESTORE_SONGS)
                }
            }.show()
        }
        driveSyncLayout.setOnClickListener { driveSyncCheckbox.toggle() }
        driveSyncCheckbox.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                startActivityForResult(GoogleHelper.getSignInClient(this).signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
            } else {
                GoogleHelper.signOut(this)
                Prefs.putBoolean(Constants.PREF_DRIVE_SYNC, false)
            }
        }
        preferSharpToogle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPreferSharp(isChecked)
        }
        customizeTabLayout.setOnClickListener {
            startActivity(Intent(this, CustomizeTabActivity::class.java))
        }
        rateAppLayout.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }
        feedbackLayout.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    intent.data = Uri.parse("mailto:marius.ivas1@gmail.com")
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_activity_feedback_subject))
                }
                startActivity(Intent.createChooser(intent, getString(R.string.settings_activity_feedback_chooser)))
            } catch (e: ActivityNotFoundException) {
                toast(R.string.settings_activity_toast_no_email_app)
            }
        }
        privacyPolicyLayout.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_PRIVACY_POLICY)))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Checks for storage permission and launches an activity to select a file.
     */
    private fun checkPermissionAndImport(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
        } else {
            launchImportActivity(requestCode)
        }
    }

    /**
     * Launches an activity to select a file.
     */
    private fun launchImportActivity(requestCode: Int) {
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = "*/*"
            }
            startActivityForResult(intent, requestCode)
        } catch (e: ActivityNotFoundException) {
            toast(R.string.settings_activity_toast_no_file_picker)
        }
    }

    override fun onSongsImported(count: Int) {
        val string = getString(if (count == 1) R.string.settings_activity_toast_song_imported else R.string.settings_activity_toast_songs_imported)
        toast(String.format(string, count))
    }

    @Suppress("UnstableApiUsage")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode in listOf(REQUEST_CODE_IMPORT_SONG, REQUEST_CODE_RESTORE_SONGS) && resultCode == RESULT_OK) {
            if (data?.data != null) {
                try {
                    if (ExportHelper.isMusFile(this, data.data!!)) {
                        val inputStream = contentResolver.openInputStream(data.data!!)!!
                        val fileJson = CharStreams.toString(InputStreamReader(inputStream, Charsets.UTF_8))
                        val exportedSongs = Gson().fromJson(fileJson, ExportSongsJson::class.java)
                        val songs = exportedSongs.exportedSongs.map { exported -> exported.toSong() }
                        val songDao = Db.instance.getSongsDao()
                        lifecycleScope.launch(IO) {
                            if (requestCode == REQUEST_CODE_RESTORE_SONGS) songDao.deleteAll()
                            songDao.insertAll(songs)
                            withContext(Main) {
                                val string = getString(if (songs.size == 1) R.string.settings_activity_toast_song_imported else R.string.settings_activity_toast_songs_imported)
                                toast(String.format(string, songs.size))
                            }
                            if (songs.size > 1 && Prefs.getBoolean(Constants.PREF_DRIVE_SYNC)) {
                                finishAffinity()
                                startActivity(Intent(this@SettingsActivity, LoadingActivity::class.java).putExtra(Constants.EXTRA_AFTER_RESTORE, true))
                            }
                        }
                    } else {
                        toast(R.string.settings_activity_toast_not_mus_file)
                    }
                } catch (e: Exception) {
                    toast(R.string.settings_activity_toast_import_error)
                }
            } else {
                toast(R.string.settings_activity_toast_import_error)
            }
        } else if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN && data != null) {
            handleSignInResult(data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode in listOf(REQUEST_CODE_IMPORT_SONG, REQUEST_CODE_RESTORE_SONGS)) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImportActivity(requestCode)
            } else {
                toast(R.string.settings_activity_toast_permission_needed)
            }
        }
    }

    /**
     * Handles data received from Google sign in.
     */
    private fun handleSignInResult(data: Intent) {
        toast(R.string.settings_activity_toast_login_success)
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener {
            val credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_APPDATA)).apply { selectedAccount = it.account }
            DriveHelper.drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName(getString(R.string.app_name))
                .build()
            Prefs.putBoolean(Constants.PREF_DRIVE_SYNC, true)
            lifecycleScope.launch(IO) { DriveSync.syncAll(this, false) }
        }.addOnFailureListener {
            driveSyncCheckbox.isChecked = false
            toast(R.string.settings_activity_toast_login_failed)
        }
    }

}
