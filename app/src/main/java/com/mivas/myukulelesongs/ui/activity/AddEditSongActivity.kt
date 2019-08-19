package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.viewmodel.AddEditSongViewModel
import com.mivas.myukulelesongs.viewmodel.AddEditSongViewModelFactory
import kotlinx.android.synthetic.main.activity_add_edit_song.*
import org.jetbrains.anko.*

class AddEditSongActivity : AppCompatActivity() {

    private lateinit var viewModel: AddEditSongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_song)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_plus)

        if (intent.hasExtra(EXTRA_ID)) {
            val viewModelFactory = AddEditSongViewModelFactory(application, intent.getLongExtra(EXTRA_ID, -1))
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)

            title = getString(R.string.add_edit_song_activity_text_edit_song)
        } else {
            val viewModelFactory = AddEditSongViewModelFactory(application, -1)
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)

            title = getString(R.string.add_edit_song_activity_text_add_song)
        }
        if (intent.hasExtra(EXTRA_ID)) {
            initObservers()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_song, menu)
        val deleteButton = menu!!.findItem(R.id.action_delete_song)
        deleteButton.isVisible = intent.hasExtra(EXTRA_ID)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_song -> {
                alert(R.string.add_edit_song_activity_dialog_delete_song_description, R.string.add_edit_song_activity_dialog_delete_song_title) {
                    negativeButton(R.string.generic_cancel) {}
                    positiveButton(R.string.generic_delete) {
                        viewModel.deleteSong(viewModel.getSong().value!!)
                        finish()
                    }
                }.show()
                true
            }
            R.id.action_save_song -> {
                saveSong()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initObservers() {
        viewModel.getSong().observe(this, Observer<Song> {
            it?.run {
                titleField.setText(title)
                authorField.setText(author)
                tabField.setText(tab)
            }
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