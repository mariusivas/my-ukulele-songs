package com.mivas.myukulelesongs.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexboxLayout
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.listeners.TransposerListener
import com.mivas.myukulelesongs.model.ChordTabData
import com.mivas.myukulelesongs.ui.dialog.ChordInfoDialog
import com.mivas.myukulelesongs.util.*
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.viewmodel.TabViewModel
import com.mivas.myukulelesongs.viewmodel.factory.SongViewModelFactory
import kotlinx.android.synthetic.main.activity_tab.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * Activity displaying a song tab.
 */
class TabActivity : AppCompatActivity(R.layout.activity_tab), TransposerListener {

    private lateinit var viewModel: TabViewModel
    private var maxCharsLine = 0

    private val customizationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            initStyles()
            val chordTabData = viewModel.getChordsTabData(viewModel.song.value!!.tab, maxCharsLine)
            tabText.setText(chordTabData.spannableBuilder, TextView.BufferType.SPANNABLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initTabScroller()
        initTransposer()
        initObservers()

        registerReceiver(customizationsReceiver, IntentFilter(Constants.BROADCAST_CUSTOMIZATIONS_UPDATED))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_auto_scroll -> {
                tabScrollerPanel.setVisible(!tabScrollerPanel.getVisible())
                true
            }
            R.id.action_edit_song -> {
                startActivity(Intent(this, AddEditSongActivity::class.java).apply {
                    putExtra(EXTRA_ID, intent.getLongExtra(EXTRA_ID, -1))
                })
                true
            }
            R.id.action_transpose -> {
                tabScrollerPanel.setVisible(false)
                transposerPanel.initialText = viewModel.song.value!!.tab
                transposerPanel.setVisible(true)
                supportActionBar?.hide()
                true
            }
            R.id.action_export_mus -> {
                val songsList = listOf(viewModel.song.value!!)
                val fileName = "${viewModel.song.value!!.title}.mus"
                ExportHelper.launchExportMusIntent(this, songsList, fileName)
                true
            }
            R.id.action_export_txt -> {
                val text = viewModel.song.value!!.tab
                val fileName = "${viewModel.song.value!!.title}.txt"
                ExportHelper.launchExportTxtIntent(this, text, fileName)
                true
            }
            R.id.action_customize -> {
                startActivity(Intent(this, CustomizeTabActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * ViewModel initializer.
     */
    private fun initViewModel() {
        val viewModelFactory = SongViewModelFactory(intent.getLongExtra(EXTRA_ID, -1))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TabViewModel::class.java)
    }

    /**
     * TabScrollerView initializer.
     */
    private fun initTabScroller() {
        tabScrollerPanel.scrollView = scrollView
    }

    /**
     * TransposerView initializer.
     */
    private fun initTransposer() {
        transposerPanel.listener = this
    }

    /**
     * Observers initializer.
     */
    private fun initObservers() {
        viewModel.song.observe(this, Observer<Song> {
            it?.run {
                this@TabActivity.title = it.title
                initStyles()
                tabText.text = Constants.TAB_FILLER
                tabText.post {
                    maxCharsLine = viewModel.getMaxCharsPerLine(tabText)
                    val text = it.tab
                    val chordsTabData = viewModel.getChordsTabData(text, maxCharsLine)
                    keyText.text = viewModel.getSongKey(chordsTabData)
                    initOriginalKey()
                    initChords(chordsTabData)
                    strummingPatternsText.text = it.strummingPatterns
                    strummingPatternsLayout.visibility = if (it.strummingPatterns.isEmpty()) View.GONE else View.VISIBLE
                    pickingPatternsText.text = it.pickingPatterns
                    pickingPatternsLayout.visibility = if (it.pickingPatterns.isEmpty()) View.GONE else View.VISIBLE
                    tabText.setText(chordsTabData.spannableBuilder, TextView.BufferType.SPANNABLE)
                }
            } ?: finish()
        })
    }

    /**
     * Styles initializer.
     */
    private fun initStyles() {
        tabText.textSize = viewModel.getTextSizeChords().toFloat()
        tabText.textColor = viewModel.getTextColorChords()
        tabParent.backgroundColor = viewModel.getBackgroundColorChords()
    }

    /**
     * Chords initializer.
     */
    private fun initChords(chordData: ChordTabData) {
        chordsFlexLayout.removeAllViews()
        chordData.chords.toSet().forEach { chord ->
            val margin = DimensionUtils.dpToPx(2)
            val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(margin, margin, margin, margin)
            val chordText = LayoutInflater.from(this).inflate(R.layout.list_item_chord, null) as TextView
            chordText.layoutParams = layoutParams
            chordText.text = chord
            chordText.setOnClickListener {
                SoundPlayer.playChord(this, chord.toFlats())
            }
            chordText.setOnLongClickListener {
                if (NetworkUtils.isInternetAvailable()) {
                    startActivity(Intent(this, ChordInfoActivity::class.java).apply {
                        putExtra(Constants.EXTRA_CHORD, chord)
                    })
                } else {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.addToBackStack(null)
                    ChordInfoDialog(chord).show(transaction, "")
                }
                true
            }
            chordsFlexLayout.addView(chordText)
        }
        chordsLayout.visibility = if (chordData.chords.isEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Original key initializer.
     */
    private fun initOriginalKey() {
        val original = viewModel.song.value!!.originalKey
        originalKeyText.text = original
        originalKeyText.visibility = if (original.isEmpty()) View.GONE else View.VISIBLE
        originalKeyLabel.visibility = if (original.isEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Updates the transposed tab.
     */
    private fun updateTransposed(transposedText: String) {
        val chordsTabData = viewModel.getChordsTabData(transposedText, maxCharsLine)
        keyText.text = viewModel.getSongKey(chordsTabData)
        initChords(chordsTabData)
        tabText.setText(chordsTabData.spannableBuilder, TextView.BufferType.SPANNABLE)
    }

    override fun onTextTransposed(transposedText: String, final: Boolean) {
        if (final) {
            if (transposedText.isEmpty()) {
                updateTransposed(viewModel.song.value!!.tab)
                supportActionBar?.show()
            } else {
                val song = viewModel.song.value!!.apply {
                    tab = transposedText
                    version = viewModel.song.value!!.version + 1
                }
                viewModel.updateSong(song)
                supportActionBar?.show()
                if (DriveSync.isActive()) DriveSync.syncUpdatedSong((song))
            }
        } else {
            updateTransposed(transposedText)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(customizationsReceiver)
        super.onDestroy()
    }

}