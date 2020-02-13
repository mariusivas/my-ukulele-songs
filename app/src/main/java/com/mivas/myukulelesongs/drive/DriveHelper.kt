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

/**
 * Helper class that handles CRUD with Google Drive
 */
object DriveHelper {

    lateinit var drive: Drive

    /**
     * Reads the config file from Google Drive.
     *
     * @param fileId The file id of the config file
     */
    fun readConfig(fileId: String): Config {
        val content = readFile(fileId)
        return Gson().fromJson(content, Config::class.java)
    }

    /**
     * Searches Google Drive for a file named config.json and returns its id if found.
     *
     * @return The file id of the config file or null of no file was found
     */
    fun getConfigId(): String? {
        val result = drive.files().list().setQ("name='${Constants.CONFIG_FILE_NAME}'").setSpaces("appDataFolder").setFields("files(id)").execute()
        return if (result.files.isEmpty()) null else result.files[0].id
    }

    /**
     * Creates an empty config file on Google Drive.
     *
     * @return The file id of the created config file
     */
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

    /**
     * Updates the config file on Google Drive.
     *
     * @param config The content of the new config file.
     */
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

    /**
     * Reads a file from Google Drive.
     *
     * @param fileId The file id of the file
     */
    private fun readFile(fileId: String): String {
        val inputStream = drive.files().get(fileId).executeMediaAsInputStream()
        return CharStreams.toString(InputStreamReader(inputStream, Charsets.UTF_8))
    }

    /**
     * Creates an empty file in internal storage.
     *
     * @param fileName The name of the file
     * @param text The content of the file
     * @return The empty file
     */
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