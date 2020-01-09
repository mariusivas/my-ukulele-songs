package com.mivas.myukulelesongs.drive

import com.mivas.myukulelesongs.drive.model.Config
import com.mivas.myukulelesongs.drive.model.DriveSong
import com.mivas.myukulelesongs.drive.model.MatchingSong
import com.mivas.myukulelesongs.database.Db
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

object DriveSync {

    private val songsDao = Db.instance.getSongsDao()

    fun isActive() = Prefs.getBoolean(Constants.PREF_DRIVE_SYNC)

    fun syncAll(scope: CoroutineScope) {
        val localSongs = songsDao.getAll()
        val cloudSongs = readConfig()?.songs
        if (cloudSongs != null) {
            val newConfig = mutableListOf<DriveSong>() // prepare a new config file
            newConfig.addAll(importFromCloud(localSongs, cloudSongs)) // import the cloud songs that do not exist locally
            newConfig.addAll(exportFromLocal(localSongs, cloudSongs)) // export the local files that do not exist on cloud
            newConfig.addAll(resolveConflicts(localSongs, cloudSongs)) // resolve conflicts between matching songs
            scope.launch(IO) {
                // updating config file is run on a separate coroutine in order for the app to launch faster
                try {
                    DriveHelper.updateConfig(Config(newConfig)) // update the config file
                    songsDao.deleteAll(songsDao.getDeleted()) // delete all songs that have been marked as deleted
                    val notUploaded = songsDao.getNotUploaded() // update the uploaded = true flag for all songs that don't have it
                    notUploaded.forEach { it.uploaded = true }
                    songsDao.updateAll(notUploaded)
                } catch (e: Exception) {
                    // if update config fails, well, let's hope it doesn't fail, but shouldn't be a problem at next startup sync
                }
            }
        }
    }

    private fun readConfig(): Config? {
        try {
            val configId = Prefs.getString(Constants.PREF_DRIVE_CONFIG_FILE_ID) // get the config id from prefs
            return if (configId.isNotEmpty()) {
                DriveHelper.readConfig(configId) // read the config file from cloud
            } else {
                val cfgId = DriveHelper.getConfigId() // search on cloud for a file named config.json
                if (cfgId != null) { // if config file was found
                    Prefs.putString(Constants.PREF_DRIVE_CONFIG_FILE_ID, cfgId) // store the found config id into prefs
                    DriveHelper.readConfig(cfgId) // read the config file from cloud
                } else {
                    val id = DriveHelper.createConfig() // create a config file on cloud
                    Prefs.putString(Constants.PREF_DRIVE_CONFIG_FILE_ID, id) // store its file id into prefs
                    Config(mutableListOf()) // return an empty list of config songs
                }
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun importFromCloud(localSongs: List<Song>, cloudSongs: List<DriveSong>): List<DriveSong> {
        val newConfigSongs = mutableListOf<DriveSong>() // prepare a list of config songs
        val newCloudSongs = cloudSongs.filter { cloud -> localSongs.none { local -> local.uniqueId == cloud.uniqueId } } // filter the songs that are on the cloud but not on local
        newCloudSongs.forEach {
            // for each song on the cloud
            songsDao.insert(it.toSong()) // insert the song into db
            newConfigSongs.add(it) // add the new song to the config
        }
        return newConfigSongs // return the config
    }

    private fun exportFromLocal(localSongs: List<Song>, cloudSongs: List<DriveSong>): List<DriveSong> {
        val newConfigSongs = mutableListOf<DriveSong>() // prepare a list of config songs
        val newLocalSongs = localSongs.filter { local -> cloudSongs.none { cloud -> cloud.uniqueId == local.uniqueId } } // filter the songs that are on local but not on the cloud
        newLocalSongs.forEach {
            // for each local song
            if (it.uploaded) { // if it's on local and not on cloud and has the uploaded flag, it means some other device deleted the song
                songsDao.delete(it) // so just delete it
            } else {
                newConfigSongs.add(it.toDriveSong()) // add the new song to the config
            }
        }
        return newConfigSongs // return the config
    }

    private fun resolveConflicts(localSongs: List<Song>, cloudSongs: List<DriveSong>): List<DriveSong> {
        val newConfigSongs = mutableListOf<DriveSong>() // prepare a list of config songs
        val matchingSongs = getMatchingSongs(localSongs, cloudSongs) // get the list of matching songs
        matchingSongs.forEach {
            when {
                it.local.version > it.cloud.version && !it.local.deleted -> {
                    newConfigSongs.add(it.local.toDriveSong()) // add the updated song to the config
                }
                it.local.version < it.cloud.version -> {
                    updateLocalSongWithCouldData(it.local, it.cloud) // update the local song with the data from the cloud song
                    songsDao.update(it.local) // update the local song into db
                    newConfigSongs.add(it.cloud) // add the song to the config
                }
                !it.local.deleted -> { // versions are the same, no changes (and local wasn't deleted ofc)
                    if (sameVersionsButDifferentContent(it.local, it.cloud)) { // in the rare case where the versions are the same but the content is different
                        updateLocalSongWithCouldData(it.local, it.cloud)
                        songsDao.update(it.local) // update the local song into db
                    }
                    newConfigSongs.add(it.cloud) // add the song to the config
                }
            }
        }
        return newConfigSongs // return the config songs
    }

    private fun getMatchingSongs(localSongs: List<Song>, cloudSongs: List<DriveSong>): List<MatchingSong> {
        val matchingSongs = mutableListOf<MatchingSong>() // prepare a list of matching songs
        localSongs.forEach { local ->
            val matchingCloud = cloudSongs.find { it.uniqueId == local.uniqueId } // find a cloud song that has the same unique id
            if (matchingCloud != null) {
                matchingSongs.add(MatchingSong(local, matchingCloud)) // if a matching cloud song was found, add it to the list
            }
        }
        return matchingSongs // return the list
    }

    fun syncCreatedSong(song: Song) = CoroutineScope(IO).launch {
        val config = readConfig() // read the config file
        if (config != null) {
            config.songs.add(song.toDriveSong()) // add the song to the config
            try {
                DriveHelper.updateConfig(config) // update the config
                songsDao.update(song.apply { uploaded = true }) // mark the song as uploaded
            } catch (e: Exception) {
                // if update config fails, the song will stay uploaded = false and will be uploaded next time at startup
            }
        }
    }

    fun syncUpdatedSong(song: Song) = CoroutineScope(IO).launch {
        val config = readConfig() // read the config file
        if (config != null) {
            val foundConfig = config.songs.find { it.uniqueId == song.uniqueId } // search for existing song in the config
            if (foundConfig != null) { // if the song was found
                with(foundConfig) {
                    // update the config song with the data from the local song
                    title = song.title
                    author = song.author
                    type = song.type
                    strummingPatterns = song.strummingPatterns
                    pickingPatterns = song.pickingPatterns
                    originalKey = song.originalKey
                    tab = song.tab
                    version = song.version
                }
                try {
                    DriveHelper.updateConfig(config) // update the config
                } catch (e: Exception) {
                    // if update config fails, the song will stay in local db with increased version and will be uploaded next time at startup
                }
            } else { // if the song was not found in the config
                config.songs.add(song.toDriveSong()) // just create a song in the config
                try {
                    DriveHelper.updateConfig(config) // update the config
                    songsDao.update(song.apply { uploaded = true }) // // mark the song as uploaded
                } catch (e: Exception) {
                    // if update config fails, the song will stay uploaded = false and will be uploaded next time at startup
                }
            }
        }
    }

    fun syncDeletedSong(song: Song) = CoroutineScope(IO).launch {
        val config = readConfig() // read the config file
        if (config != null) {
            val foundConfig = config.songs.find { it.uniqueId == song.uniqueId } // search for existing song in the config
            if (foundConfig != null) {
                config.songs.remove(foundConfig) // remove the config song from the config
                try {
                    DriveHelper.updateConfig(config) // update the config
                    songsDao.delete(song) // delete the local song
                } catch (e: Exception) {
                    // if update config fails, the song will stay marked as deleted and will be removed next time at startup
                }
            }
        }
    }

    private fun sameVersionsButDifferentContent(local: Song, cloud: DriveSong): Boolean {
        return local.version == cloud.version &&
                (local.title != cloud.title ||
                        local.author != cloud.author ||
                        local.type != cloud.type ||
                        local.strummingPatterns != cloud.strummingPatterns ||
                        local.pickingPatterns != cloud.pickingPatterns ||
                        local.originalKey != cloud.originalKey ||
                        local.tab != cloud.tab)
    }

    private fun updateLocalSongWithCouldData(local: Song, cloud: DriveSong) {
        local.apply {
            title = cloud.title
            author = cloud.author
            type = cloud.type
            strummingPatterns = cloud.strummingPatterns
            pickingPatterns = cloud.pickingPatterns
            originalKey = cloud.originalKey
            tab = cloud.tab
            version = cloud.version
            deleted = false
            uniqueId = cloud.uniqueId
        }
    }

}