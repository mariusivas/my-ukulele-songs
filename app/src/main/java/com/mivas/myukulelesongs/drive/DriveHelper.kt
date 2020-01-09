package com.mivas.myukulelesongs.drive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.common.io.CharStreams
import com.google.common.io.Files
import com.google.gson.Gson
import com.mivas.myukulelesongs.App
import com.mivas.myukulelesongs.drive.model.Config
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import java.io.IOException
import java.io.InputStreamReader

object DriveHelper {

    lateinit var drive: Drive

    fun readConfig(fileId: String): Config {
        val content = readFile(fileId)
        return Gson().fromJson(content, Config::class.java)
    }

    fun getConfigId(): String? {
        val result = drive.files().list().setQ("name='${Constants.CONFIG_FILE_NAME}'").setSpaces("appDataFolder").setFields("files(id)").execute()
        return if (result.files.isEmpty()) null else result.files[0].id
    }

    fun createConfig(): String {
        val fileMetadata = File().apply {
            name = Constants.CONFIG_FILE_NAME
            parents = listOf("appDataFolder")
        }
        val localFile = createTempFile(Constants.CONFIG_FILE_NAME, Constants.CONFIG_FILE_DEFAULT_VALUE)
        val mediaContent = FileContent("application/json", localFile)
        val file = drive.files().create(fileMetadata, mediaContent).setFields("id").execute()
        localFile.delete()
        return file.id
    }

    fun updateConfig(config: Config) {
        val fileMetadata = File().apply {
            name = Constants.CONFIG_FILE_NAME
        }
        val localFile = createTempFile(Constants.CONFIG_FILE_NAME, Gson().toJson(config, Config::class.java))
        val mediaContent = FileContent("application/json", localFile)
        val configId = Prefs.getString(Constants.PREF_DRIVE_CONFIG_FILE_ID)
        drive.files().update(configId, fileMetadata, mediaContent).execute()
        localFile.delete()
    }

    private fun readFile(driveId: String): String {
        val inputStream = drive.files().get(driveId).executeMediaAsInputStream()
        return CharStreams.toString(InputStreamReader(inputStream, Charsets.UTF_8))
    }

    private fun createTempFile(fileName: String, text: String): java.io.File {
        val exportDir = java.io.File(App.instance.applicationContext.filesDir, "Temp")
        if (!exportDir.exists()) {
            exportDir.mkdir()
        }
        val file = java.io.File(exportDir, fileName)
        try {
            Files.asCharSink(file, com.google.common.base.Charsets.UTF_8).write(text)
        } catch (e: IOException) {
        }
        return file
    }


}