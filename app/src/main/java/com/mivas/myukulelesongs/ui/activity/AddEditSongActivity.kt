package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
            val viewModelFactory =
                AddEditSongViewModelFactory(application, intent.getLongExtra(EXTRA_ID, -1))
            viewModel =
                ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)

            title = getString(R.string.add_edit_song_activity_text_edit_song)
        } else {
            val viewModelFactory = AddEditSongViewModelFactory(application, -1)
            viewModel =
                ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)

            title = getString(R.string.add_edit_song_activity_text_add_song)
        }
        initListeners()
        initObservers()
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
                alert(
                    R.string.add_edit_song_activity_dialog_delete_song_description,
                    R.string.add_edit_song_activity_dialog_delete_song_title
                ) {
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

    private fun initListeners() {
        strummingButton.setOnClickListener { viewModel.selectedType.value = 0 }
        pickingButton.setOnClickListener { viewModel.selectedType.value = 1 }
        strummingPickingButton.setOnClickListener { viewModel.selectedType.value = 2 }
    }

    private fun initObservers() {
        if (intent.hasExtra(EXTRA_ID)) {
            viewModel.getSong().observe(this, Observer<Song> {
                it?.run {
                    titleField.setText(title)
                    authorField.setText(author)
                    viewModel.selectedType.value = type
                    strummingPatternsField.setText(strummingPatterns)
                    pickingPatternsField.setText(pickingPatterns)
                    tabField.setText(tab)
                }
            })
        }
        viewModel.selectedType.observe(this, Observer<Int> {
            strummingPatternsField.visibility = if (it == 0 || it == 2) View.VISIBLE else View.GONE
            pickingPatternsField.visibility = if (it == 1 || it == 2) View.VISIBLE else View.GONE
            strummingButton.background =
                if (viewModel.selectedType.value!! == 0) getDrawable(R.drawable.background_mahogany_medium) else getDrawable(
                    R.drawable.background_mahogany_light
                )
            pickingButton.background =
                if (viewModel.selectedType.value!! == 1) getDrawable(R.drawable.background_mahogany_medium) else getDrawable(
                    R.drawable.background_mahogany_light
                )
            strummingPickingButton.background =
                if (viewModel.selectedType.value!! == 2) getDrawable(R.drawable.background_mahogany_medium) else getDrawable(
                    R.drawable.background_mahogany_light
                )
        })
    }

    private fun saveSong() {
        if (titleField.text.toString().trim().isBlank()) {
            toast(R.string.add_edit_song_activity_toast_empty_title)
            return
        }
        if (intent.hasExtra(EXTRA_ID)) {
            val song = viewModel.getSong().value!!.run { populateSong(this) }
            viewModel.updateSong(song)
        } else {
            val song = Song().run { populateSong(this) }
            viewModel.insertSong(song)
        }
        finish()
    }

    private fun populateSong(song: Song) = song.apply {
        title = titleField.text.toString()
        author = authorField.text.toString()
        type = viewModel.selectedType.value!!
        strummingPatterns = if (type == 0 || type == 2) strummingPatternsField.text.toString() else ""
        pickingPatterns = if (type == 1 || type == 2) pickingPatternsField.text.toString() else ""
        tab = tabField.text.toString()
    }
}