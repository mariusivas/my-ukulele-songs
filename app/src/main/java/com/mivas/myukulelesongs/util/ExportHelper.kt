package com.mivas.myukulelesongs.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.Gson
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.listeners.SongsImportedListener
import com.mivas.myukulelesongs.model.ExportSongsJson
import com.mivas.myukulelesongs.model.ExportedSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.IOException

object ExportHelper {

    fun launchExportMusIntent(context: Context, songs: List<Song>, fileName: String) {
        val songFile = saveTempFile(context, fileName, exportedSongsToJsonString(songs))
        startChooser(context, songFile)
    }

    fun launchExportTxtIntent(context: Context, text: String, fileName: String) {
        val songFile = saveTempFile(context, fileName, text)
        startChooser(context, songFile)
    }

    fun isMusFile(context: Context, uri: Uri): Boolean {
        val fileName = getFileName(context, uri)
        return Files.getFileExtension(fileName) == "mus"
    }

    private fun startChooser(context: Context, songFile: File) {
        val contentUri = FileProvider.getUriForFile(context, "com.mivas.myukulelesongs.fileprovider", songFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            type = "file/*"
            putExtra(Intent.EXTRA_STREAM, contentUri)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.tab_activity_text_export_with)))
    }

    private fun saveTempFile(context: Context, fileName: String, text: String): File {
        val exportDir = File(context.filesDir, "Songs")
        if (!exportDir.exists()) { exportDir.mkdir() }
        val file = File(exportDir, fileName)
        try {
            Files.asCharSink(file, Charsets.UTF_8).write(text)
        } catch (e: IOException) {
            context.toast(context.getString(R.string.settings_activity_toast_backup_error))
        }
        return file
    }

    private fun exportedSongsToJsonString(songs: List<Song>): String {
        val exportedSongs = ExportSongsJson(songs.map { ExportedSong(it) })
        return Gson().toJson(exportedSongs, ExportSongsJson::class.java)
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}