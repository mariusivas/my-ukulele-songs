package com.mivas.myukulelesongs.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.listeners.SongsFragmentListener
import com.mivas.myukulelesongs.ui.activity.AddEditSongActivity
import com.mivas.myukulelesongs.ui.activity.TabActivity
import com.mivas.myukulelesongs.ui.adapter.SongsAdapter
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.KeyboardUtils
import com.mivas.myukulelesongs.viewmodel.SongsViewModel
import kotlinx.android.synthetic.main.fragment_songs.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast


class SongsFragment : Fragment(), SongsFragmentListener {

    private lateinit var viewModel: SongsViewModel
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_songs_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_song -> {
                viewModel.toggleSearchMode()
                true
            }
            R.id.action_add_song -> {
                startActivity(Intent(activity, AddEditSongActivity::class.java))
                true
            }
            R.id.action_randomize -> {
                try {
                    val song = viewModel.getRandomSong()
                    startActivity(Intent(activity, TabActivity::class.java).apply { putExtra(Constants.EXTRA_ID, song.id) })
                } catch (e: NoSuchElementException) {
                    activity!!.toast(R.string.songs_fragment_toast_no_songs)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(this, viewModel.backPressedCallback)
        viewModel.backPressedCallback.isEnabled = false
    }

    private fun initViews() {
        songsAdapter = SongsAdapter(activity!!, this)
        with(songsRecycler) {
            layoutManager = LinearLayoutManager(activity)
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
                KeyboardUtils.focusEditText(activity!!, searchField)
            } else {
                KeyboardUtils.closeKeyboard(activity)
                searchField.setText("")
                searchView.visibility = View.GONE
            }
        })
    }

    override fun onSongClicked(song: Song) {
        startActivity(Intent(activity, TabActivity::class.java).apply { putExtra(Constants.EXTRA_ID, song.id) })
    }

    override fun onSongEditClicked(song: Song) {
        startActivity(Intent(activity, AddEditSongActivity::class.java).apply { putExtra(Constants.EXTRA_ID, song.id) })
    }

    override fun onSongDeleteClicked(song: Song) {
        activity!!.alert(R.string.add_edit_song_activity_dialog_delete_song_description, R.string.add_edit_song_activity_dialog_delete_song_title) {
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

}