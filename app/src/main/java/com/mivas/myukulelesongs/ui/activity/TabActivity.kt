package com.mivas.myukulelesongs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.viewmodel.TabViewModel
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.util.Prefs
import com.mivas.myukulelesongs.viewmodel.TabViewModelFactory
import kotlinx.android.synthetic.main.activity_tab.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

class TabActivity : AppCompatActivity() {

    private lateinit var viewModel: TabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        val viewModelFactory = TabViewModelFactory(application, intent.getLongExtra(EXTRA_ID, -1))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TabViewModel::class.java)

        initStyles()
        initObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_song -> {
                startActivity(Intent(this, AddEditSongActivity::class.java).apply {
                    putExtra(EXTRA_ID, intent.getLongExtra(EXTRA_ID, -1))
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initObservers() {
        viewModel.getSong().observe(this, Observer<Song> {
            it?.run {
                this@TabActivity.title = it.title
                val chordData = viewModel.getChordData(it)
                chordsText.text = viewModel.getDisplayChords(chordData)
                strummingPatternsText.text = it.strummingPatterns
                strummingPatternsLayout.visibility = if (it.strummingPatterns.isEmpty()) View.GONE else View.VISIBLE
                tabText.setText(chordData.spannableBuilder, TextView.BufferType.SPANNABLE)
            } ?: finish()
        })
    }

    private fun initStyles() {
        tabText.textSize = Prefs.getFloat(Constants.PREF_TAB_TEXT_SIZE, Constants.DEFAULT_TAB_TEXT_SIZE)
        tabText.textColor = Prefs.getInt(Constants.PREF_TAB_TEXT_COLOR, Constants.DEFAULT_TAB_TEXT_COLOR)
        tabParent.backgroundColor = Prefs.getInt(Constants.PREF_TAB_BACKGROUND_COLOR, Constants.DEFAULT_TAB_BACKGROUND_COLOR)
    }

}
