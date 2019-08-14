package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.viewmodel.AddEditSongViewModel
import com.mivas.myukulelesongs.viewmodel.AddEditSongViewModelFactory
import kotlinx.android.synthetic.main.activity_add_edit_song.*
import org.jetbrains.anko.toast

class AddEditSongActivity : AppCompatActivity() {

    private lateinit var viewModel: AddEditSongViewModel

    companion object {
        const val EXTRA_ID = "com.mivas.myukulelesongs.EXTRA_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_song)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_plus)

        if (intent.hasExtra(EXTRA_ID)) {
            val viewModelFactory = AddEditSongViewModelFactory(application, intent.getLongExtra(EXTRA_ID, 0))
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)

            title = getString(R.string.add_edit_song_activity_text_edit_song)
        } else {
            val viewModelFactory = AddEditSongViewModelFactory(application, 0)
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)

            title = getString(R.string.add_edit_song_activity_text_add_song)
        }
        if (intent.hasExtra(EXTRA_ID)) {
            initObservers()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_song, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_song -> {
                saveSong()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initObservers() {
        viewModel.getSong().observe(this, Observer<Song> {
            titleField.setText(it.title)
            authorField.setText(it.author)
            tabField.setText(it.tab)
        })
    }

    private fun saveSong() {
        if (titleField.text.toString().trim().isBlank()) {
            toast(R.string.add_edit_song_activity_toast_empty_title)
            return
        }

        if (intent.hasExtra(EXTRA_ID)) {
            val song = viewModel.getSong().value!!.apply {
                title = titleField.text.toString()
                author = authorField.text.toString()
                tab = tabField.text.toString()
            }
            viewModel.updateSong(song)
        } else {
            val song = Song().apply {
                title = titleField.text.toString()
                author = authorField.text.toString()
                tab = tabField.text.toString()
            }
            viewModel.insertSong(song)
        }
        finish()
    }
}