package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.listeners.KeyPickerListener
import com.mivas.myukulelesongs.ui.dialog.KeyPickerDialog
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.util.Constants.EXTRA_IS_TAB
import com.mivas.myukulelesongs.util.IdUtils
import com.mivas.myukulelesongs.viewmodel.AddEditSongViewModel
import com.mivas.myukulelesongs.viewmodel.factory.AddEditSongViewModelFactory
import kotlinx.android.synthetic.main.activity_add_edit_song.*
import kotlinx.android.synthetic.main.activity_add_edit_song.originalKeyText
import org.jetbrains.anko.*

class AddEditSongActivity : AppCompatActivity(), KeyPickerListener {

    private lateinit var viewModel: AddEditSongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_song)

        initViewModel()
        initType()
        initActionBar()
        initViews()
        initListeners()
        initObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_song, menu)
        val deleteButton = menu!!.findItem(R.id.action_delete_song)
        deleteButton.isVisible = viewModel.isEdit
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_song -> {
                showDeleteSongDialog()
                true
            }
            R.id.action_save_song -> {
                saveSong()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViewModel() {
        val viewModelFactory = AddEditSongViewModelFactory(intent.getLongExtra(EXTRA_ID, -1))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel::class.java)
    }

    private fun initActionBar() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(if (viewModel.isEdit) R.string.add_edit_song_activity_text_edit_song else R.string.add_edit_song_activity_text_add_song)
        }
    }

    private fun initViews() {
        tabField.setHorizontallyScrolling(true)
        if (!viewModel.isEdit && viewModel.selectedType.value == 3) {
            tabField.setText(Constants.TAB_DEFAULT_TEXT)
        }
    }

    private fun initListeners() {
        strummingButton.setOnClickListener { viewModel.selectedType.value = 0 }
        pickingButton.setOnClickListener { viewModel.selectedType.value = 1 }
        bothButton.setOnClickListener { viewModel.selectedType.value = 2 }
        selectKeyButton.setOnClickListener { showKeySelectionDialog() }
    }

    private fun initObservers() {
        if (viewModel.isEdit) {
            viewModel.song.observe(this, Observer {
                it?.run {
                    titleField.setText(title)
                    authorField.setText(author)
                    originalKeyText.text = originalKey
                    viewModel.selectedType.value = type
                    strummingPatternsField.setText(strummingPatterns)
                    pickingPatternsField.setText(pickingPatterns)
                    tabField.setText(tab)
                }
            })
        }
        viewModel.selectedType.observe(this, Observer {
            if (viewModel.selectedType.value == 3) {
                selectTypeLayout.visibility = View.GONE
                strummingPatternsField.visibility = View.GONE
                pickingPatternsField.visibility = View.GONE
                tabField.hint = getString(R.string.add_edit_song_activity_hint_tab)
            } else {
                strummingPatternsField.visibility = if (it == 0 || it == 2) View.VISIBLE else View.GONE
                pickingPatternsField.visibility = if (it == 1 || it == 2) View.VISIBLE else View.GONE
                strummingButton.background = getDrawable(if (viewModel.selectedType.value!! == 0) R.drawable.background_mahogany_medium else R.drawable.background_mahogany_light)
                pickingButton.background = getDrawable(if (viewModel.selectedType.value!! == 1) R.drawable.background_mahogany_medium else R.drawable.background_mahogany_light)
                bothButton.background = getDrawable(if (viewModel.selectedType.value!! == 2) R.drawable.background_mahogany_medium else R.drawable.background_mahogany_light)
                tabField.hint = getString(R.string.add_edit_song_activity_hint_chords)
            }
        })
    }

    private fun initType() {
        if (intent.hasExtra(EXTRA_IS_TAB) && intent.getBooleanExtra(EXTRA_IS_TAB, false)) {
            viewModel.selectedType.value = 3
        }
    }

    private fun showKeySelectionDialog() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        KeyPickerDialog(this).show(transaction, "")
    }

    private fun showDeleteSongDialog() {
        alert(R.string.add_edit_song_activity_dialog_delete_song_description, R.string.add_edit_song_activity_dialog_delete_song_title) {
            negativeButton(R.string.generic_cancel) {}
            positiveButton(R.string.generic_delete) {
                if (DriveSync.isActive()) {
                    viewModel.updateSong(viewModel.song.value!!.apply { deleted = true })
                    DriveSync.syncDeletedSong(viewModel.song.value!!)
                } else {
                    viewModel.deleteSong(viewModel.song.value!!)
                }
                finish()
            }
        }.show()
    }

    private fun saveSong() {
        if (titleField.text.toString().trim().isBlank()) {
            toast(R.string.add_edit_song_activity_toast_empty_title)
            return
        }
        if (viewModel.isEdit) {
            val song = viewModel.song.value!!.run { updateSongData(this) }
            viewModel.updateSong(song)
            if (DriveSync.isActive()) DriveSync.syncUpdatedSong((song))
        } else {
            val song = Song().run { updateSongData(this) }
            viewModel.insertSong(song)
            if (DriveSync.isActive()) DriveSync.syncCreatedSong((song))
        }
        finish()
    }

    private fun updateSongData(song: Song) = song.apply {
        title = titleField.text.toString()
        author = authorField.text.toString()
        type = viewModel.selectedType.value!!
        strummingPatterns = if (type == 0 || type == 2) strummingPatternsField.text.toString() else ""
        pickingPatterns = if (type == 1 || type == 2) pickingPatternsField.text.toString() else ""
        originalKey = originalKeyText.text.toString()
        tab = tabField.text.toString()
        if (viewModel.isEdit) {
            version = song.version + 1
        } else {
            uniqueId = IdUtils.generateUniqueId()
        }
    }

    override fun onKeyClicked(key: String) {
        originalKeyText.text = key
    }

}