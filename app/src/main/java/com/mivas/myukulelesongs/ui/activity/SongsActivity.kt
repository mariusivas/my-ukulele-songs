package com.mivas.myukulelesongs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.listeners.SongsActivityListener
import com.mivas.myukulelesongs.ui.adapter.SongsAdapter
import com.mivas.myukulelesongs.viewmodel.SongsViewModel
import kotlinx.android.synthetic.main.activity_songs.*
import org.jetbrains.anko.toast

class SongsActivity : AppCompatActivity(), SongsActivityListener {

    private lateinit var viewModel: SongsViewModel
    private lateinit var songsAdapter: SongsAdapter

    companion object {
        const val ADD_SONG_REQUEST = 1
        const val EDIT_SONG_REQUEST = 2
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_songs_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_song -> {
                true
            }
            R.id.action_add_song -> {
                startActivity(Intent(this, AddEditSongActivity::class.java))
                true
            }
            R.id.action_randomize -> {
                true
            }
            R.id.action_settings -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs)

        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() {
        viewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        songsAdapter = SongsAdapter(this, this)
        with (songsRecycler) {
            layoutManager = LinearLayoutManager(this@SongsActivity)
            adapter = songsAdapter
        }
    }

    private fun initListeners() {

    }

    private fun initObservers() {
        viewModel.getAllSongs().observe(this, Observer<List<Song>> {
            songsAdapter.submitList(it)
        })
    }

    override fun onSongClicked(song: Song) {
        toast("click")
    }

    override fun onSongEditClicked(song: Song) {
        val intent = Intent(this, AddEditSongActivity::class.java).apply {
            putExtra(AddEditSongActivity.EXTRA_ID, song.id)
        }
        startActivity(intent)
    }

    override fun onSongDeleteClicked(song: Song) {
        toast("delete")
    }
}
