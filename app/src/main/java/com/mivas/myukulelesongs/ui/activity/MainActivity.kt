package com.mivas.myukulelesongs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.listeners.SongsActivityListener
import com.mivas.myukulelesongs.ui.adapter.SongsAdapter
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.util.KeyboardUtils
import com.mivas.myukulelesongs.viewmodel.SongsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), SongsActivityListener {

    private lateinit var viewModel: SongsViewModel
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_song -> {
                viewModel.revertSearchMode()
                true
            }
            R.id.action_add_song -> {
                startActivity(Intent(this, AddEditSongActivity::class.java))
                true
            }
            R.id.action_randomize -> {
                try {
                    val song = viewModel.getRandomSong()
                    startActivity(Intent(this, TabActivity::class.java).apply { putExtra(EXTRA_ID, song.id) })
                } catch (e: NoSuchElementException) {
                    toast(R.string.songs_activity_toast_no_songs)
                }
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initViews()
        initListeners()
        initObservers()

        viewModel.checkFirstRun()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
    }

    private fun initViews() {
        songsAdapter = SongsAdapter(this, this)
        with(songsRecycler) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = songsAdapter
        }
    }

    private fun initListeners() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.filter.value = searchField.text.toString()
            }
        })
    }

    private fun initObservers() {
        viewModel.songs.observe(this, Observer {
            songsAdapter.submitList(it)
        })
        viewModel.searchMode.observe(this, Observer {
            if (it) {
                searchView.visibility = View.VISIBLE
                KeyboardUtils.focusEditText(this, searchField)
            } else {
                KeyboardUtils.closeKeyboard(this)
                searchField.setText("")
                searchView.visibility = View.GONE
            }
        })
    }

    override fun onSongClicked(song: Song) {
        startActivity(Intent(this, TabActivity::class.java).apply { putExtra(EXTRA_ID, song.id) })
    }

    override fun onSongEditClicked(song: Song) {
        startActivity(Intent(this, AddEditSongActivity::class.java).apply { putExtra(EXTRA_ID, song.id) })
    }

    override fun onSongDeleteClicked(song: Song) {
        alert(R.string.add_edit_song_activity_dialog_delete_song_description, R.string.add_edit_song_activity_dialog_delete_song_title) {
            negativeButton(R.string.generic_cancel) {}
            positiveButton(R.string.generic_delete) {
                if (DriveSync.isActive()) {
                    viewModel.updateSong(song.apply { deleted = true })
                    DriveSync.syncDeletedSong(song)
                } else {
                    viewModel.deleteSong(song)
                }
            }
        }.show()
    }

    override fun onBackPressed() {
        if (viewModel.searchMode.value!!) {
            viewModel.revertSearchMode()
        } else {
            super.onBackPressed()
        }
    }
}
