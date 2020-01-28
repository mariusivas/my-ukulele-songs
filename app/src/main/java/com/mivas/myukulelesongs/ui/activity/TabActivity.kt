package com.mivas.myukulelesongs.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexboxLayout
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.model.ChordTabData
import com.mivas.myukulelesongs.ui.dialog.ChordInfoDialog
import com.mivas.myukulelesongs.util.*
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.viewmodel.TabViewModel
import com.mivas.myukulelesongs.viewmodel.factory.SongViewModelFactory
import kotlinx.android.synthetic.main.activity_tab.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor


class TabActivity : AppCompatActivity() {

    private lateinit var viewModel: TabViewModel
    private var maxCharsLine = 0
    private val scrollHandler = Handler()
    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (scrollSeekBar.progress != 0) {
                scrollView.smoothScrollTo(0, scrollView.scrollY + 1)
            }
            scrollHandler.postDelayed(this, 105 - scrollSeekBar.progress.toLong())
        }
    }
    private val customizationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            initStyles()
            val chordTabData = viewModel.getChordsTabData(viewModel.song.value!!.tab, maxCharsLine)
            tabText.setText(chordTabData.spannableBuilder, TextView.BufferType.SPANNABLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        initViewModel()
        initViews()
        initListeners()
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
                viewModel.scrollMode.value = !viewModel.scrollMode.value!!
                true
            }
            R.id.action_edit_song -> {
                startActivity(Intent(this, AddEditSongActivity::class.java).apply {
                    putExtra(EXTRA_ID, intent.getLongExtra(EXTRA_ID, -1))
                })
                true
            }
            R.id.action_transpose -> {
                viewModel.scrollMode.value = false
                viewModel.transposeMode.value = true
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

    private fun initViewModel() {
        val viewModelFactory = SongViewModelFactory(intent.getLongExtra(EXTRA_ID, -1))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TabViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.song.observe(this, Observer<Song> {
            it?.run {
                this@TabActivity.title = it.title
                initStyles()
                tabText.text = Constants.TAB_FILLER
                tabText.post {
                    maxCharsLine = viewModel.getMaxCharsPerLine(tabText)
                    val text = if (viewModel.transposedText.isNotEmpty()) viewModel.transposedText else it.tab
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
        viewModel.scrollMode.observe(this, Observer<Boolean> {
            scrollTabView.visibility = if (it) View.VISIBLE else View.GONE
        })
        viewModel.transposeMode.observe(this, Observer<Boolean> {
            transposeView.visibility = if (it) View.VISIBLE else View.GONE
            if (it) supportActionBar?.hide() else supportActionBar?.show()
        })
    }

    private fun initViews() {
        scrollSeekBar.progress = Prefs.getInt(Constants.PREF_LAST_SCROLL_SPEED, Constants.DEFAULT_SCROLL_SPEED)
        scrollSpeedText.text = scrollSeekBar.progress.toString()
        scrollSpeedTextContainer.layoutParams = (scrollSpeedTextContainer.layoutParams as ConstraintLayout.LayoutParams).apply { horizontalBias = scrollSeekBar.progress.toFloat() / 100f }
    }

    private fun initListeners() {
        scrollPlayButton.setOnClickListener { if (viewModel.scrollRunning) stopScroll() else startScroll() }
        scrollCloseButton.setOnClickListener { viewModel.scrollMode.value = false }
        scrollSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Prefs.putInt(Constants.PREF_LAST_SCROLL_SPEED, seekBar.progress)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                scrollSpeedText.text = progress.toString()
                scrollSpeedTextContainer.layoutParams = (scrollSpeedTextContainer.layoutParams as ConstraintLayout.LayoutParams).apply { horizontalBias = progress.toFloat() / 100f }
            }
        })
        transposeCloseButton.setOnClickListener {
            viewModel.transposeMode.value = false
            viewModel.transposedText = ""
            updateTransposed()
        }
        transposeSaveButton.setOnClickListener {
            viewModel.transposeMode.value = false
            val song = viewModel.song.value!!.apply {
                tab = viewModel.transposedText
                version = viewModel.song.value!!.version + 1
            }
            viewModel.updateSong(song)
            viewModel.transposedText = ""
            if (DriveSync.isActive()) DriveSync.syncUpdatedSong((song))
        }
        transposeMinusButton.setOnClickListener {
            viewModel.transpose(false)
            updateTransposed()
        }
        transposePlusButton.setOnClickListener {
            viewModel.transpose(true)
            updateTransposed()
        }
    }

    private fun initStyles() {
        tabText.textSize = viewModel.getTextSizeChords().toFloat()
        tabText.textColor = viewModel.getTextColorChords()
        tabParent.backgroundColor = viewModel.getBackgroundColorChords()
    }

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
                    startActivity(Intent(this, ChordDataActivity::class.java).apply {
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
    }

    private fun initOriginalKey() {
        val original = viewModel.song.value!!.originalKey
        originalKeyText.text = original
        originalKeyText.visibility = if (original.isEmpty()) View.GONE else View.VISIBLE
        originalKeyLabel.visibility = if (original.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateTransposed() {
        val text = if (viewModel.transposedText.isNotEmpty()) viewModel.transposedText else viewModel.song.value!!.tab
        val chordsTabData = viewModel.getChordsTabData(text, maxCharsLine)
        keyText.text = viewModel.getSongKey(chordsTabData)
        initChords(chordsTabData)
        tabText.setText(chordsTabData.spannableBuilder, TextView.BufferType.SPANNABLE)
    }

    private fun startScroll() {
        viewModel.scrollRunning = true
        scrollPlayIcon.imageResource = R.drawable.selector_button_pause
        scrollHandler.postDelayed(scrollRunnable, 150 - scrollSeekBar.progress.toLong())
    }

    private fun stopScroll() {
        viewModel.scrollRunning = false
        scrollPlayIcon.imageResource = R.drawable.selector_button_play
        scrollHandler.removeCallbacks(scrollRunnable)
    }

    override fun onDestroy() {
        unregisterReceiver(customizationsReceiver)
        super.onDestroy()
    }

}