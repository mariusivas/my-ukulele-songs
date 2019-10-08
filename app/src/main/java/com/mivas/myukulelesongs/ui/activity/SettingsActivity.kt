package com.mivas.myukulelesongs.ui.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import com.mivas.myukulelesongs.BuildConfig
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.listeners.SongsImportedListener
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.ExportHelper
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.InputStreamReader
import java.lang.Exception

class SettingsActivity : AppCompatActivity(), SongsImportedListener {

    companion object {
        const val REQUEST_CODE_IMPORT_SONG = 1
        const val REQUEST_CODE_RESTORE_SONGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        initListeners()
    }

    private fun initViews() {
        versionDescriptionText.text = BuildConfig.VERSION_NAME
    }

    private fun initListeners() {
        importLayout.setOnClickListener {
            checkPermissionAndImport(REQUEST_CODE_IMPORT_SONG)
        }
        backupLayout.setOnClickListener {
            doAsync {
                val songs = Db.instance.getSongsDao().getAll()
                val fileName = "Backup${System.currentTimeMillis()}.mus"
                uiThread { ExportHelper.launchExportMusIntent(this@SettingsActivity, songs, fileName) }
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

    private fun checkPermissionAndImport(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
        } else {
            launchImportActivity(requestCode)
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode in listOf(REQUEST_CODE_IMPORT_SONG, REQUEST_CODE_RESTORE_SONGS) && resultCode == RESULT_OK) {
            data?.data?.let {
                try {
                    if (ExportHelper.isMusFile(this, it)) {
                        val inputStream = contentResolver.openInputStream(it)!!
                        val fileJson = CharStreams.toString(InputStreamReader(inputStream, Charsets.UTF_8))
                        val clearDb = requestCode == REQUEST_CODE_RESTORE_SONGS
                        ExportHelper.importSongs(fileJson, this, clearDb)
                    } else {
                        toast(R.string.settings_activity_toast_not_mus_file)
                    }
                } catch (e: Exception) {
                    toast(R.string.settings_activity_toast_import_error)
                }
            } ?: toast(R.string.settings_activity_toast_import_error)
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
}
