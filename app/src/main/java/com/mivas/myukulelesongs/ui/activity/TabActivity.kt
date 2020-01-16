package com.mivas.myukulelesongs.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexboxLayout
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.drive.DriveSync
import com.mivas.myukulelesongs.database.model.Song
import com.mivas.myukulelesongs.model.ChordData
import com.mivas.myukulelesongs.ui.fragment.ChordDialogFragment
import com.mivas.myukulelesongs.util.*
import com.mivas.myukulelesongs.viewmodel.TabViewModel
import com.mivas.myukulelesongs.util.Constants.EXTRA_ID
import com.mivas.myukulelesongs.viewmodel.factory.TabViewModelFactory
import kotlinx.android.synthetic.main.activity_tab.*
import org.jetbrains.anko.*


class TabActivity : AppCompatActivity() {

    private lateinit var viewModel: TabViewModel
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
            tabText.setText(viewModel.getChordData(viewModel.song.value!!.tab).spannableBuilder, TextView.BufferType.SPANNABLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        initViewModel()
        initStyles()
        initViews()
        initListeners()
        initObservers()

        registerReceiver(customizationsReceiver, IntentFilter(Constants.BROADCAST_CUSTOMIZATIONS_UPDATED))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_auto_scroll -> {
                scrollTabView.visibility = if (scrollTabView.visibility == View.GONE) View.VISIBLE else View.GONE
                true
            }
            R.id.action_edit_song -> {
                startActivity(Intent(this, AddEditSongActivity::class.java).apply {
                    putExtra(EXTRA_ID, intent.getLongExtra(EXTRA_ID, -1))
                })
                true
            }
            R.id.action_transpose -> {
                scrollTabView.visibility = View.GONE
                transposeView.visibility = View.VISIBLE
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

    private fun initViewModel() {
        val viewModelFactory = TabViewModelFactory(intent.getLongExtra(EXTRA_ID, -1))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TabViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.song.observe(this, Observer<Song> {
            it?.run {
                this@TabActivity.title = it.title
                val chordData = viewModel.getChordData(it.tab)
                keyText.text = KeyHelper.findKey(chordData.allChords)
                initOriginalKey()
                initChords(chordData)
                strummingPatternsText.text = it.strummingPatterns
                strummingPatternsLayout.visibility = if (it.strummingPatterns.isEmpty()) View.GONE else View.VISIBLE
                pickingPatternsText.text = it.pickingPatterns
                pickingPatternsLayout.visibility = if (it.pickingPatterns.isEmpty()) View.GONE else View.VISIBLE
                tabText.setText(chordData.spannableBuilder, TextView.BufferType.SPANNABLE)
            } ?: finish()
        })
        viewModel.transposedText.observe(this, Observer<String> {
            if (it.isNotEmpty()) {
                val chordData = viewModel.getChordData(it)
                keyText.text = KeyHelper.findKey(chordData.allChords)
                initChords(chordData)
                tabText.setText(chordData.spannableBuilder, TextView.BufferType.SPANNABLE)
            } else {
                viewModel.song.value?.let { song ->
                    val chordData = viewModel.getChordData(song.tab)
                    keyText.text = KeyHelper.findKey(chordData.allChords)
                    initChords(chordData)
                    tabText.setText(chordData.spannableBuilder, TextView.BufferType.SPANNABLE)
                }
            }
        })
    }

    private fun initViews() {
        scrollSeekBar.progress = Prefs.getInt(Constants.PREF_LAST_SCROLL_SPEED, Constants.DEFAULT_SCROLL_SPEED)
        scrollSpeedText.text = scrollSeekBar.progress.toString()
        scrollSpeedTextContainer.layoutParams = (scrollSpeedTextContainer.layoutParams as ConstraintLayout.LayoutParams).apply { horizontalBias = scrollSeekBar.progress.toFloat() / 100f }
    }

    private fun initListeners() {
        scrollPlayButton.setOnClickListener { if (viewModel.scrollRunning) stopScroll() else startScroll() }
        scrollCloseButton.setOnClickListener { scrollTabView.visibility = View.GONE }
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
            transposeView.visibility = View.GONE
            viewModel.transposedText.value = ""
            supportActionBar?.show()
        }
        transposeSaveButton.setOnClickListener {
            val song = viewModel.song.value!!.apply {
                tab = viewModel.transposedText.value!!
                version = viewModel.song.value!!.version + 1
            }
            viewModel.updateSong(song)
            viewModel.transposedText.value = ""
            transposeView.visibility = View.GONE
            supportActionBar?.show()
            if (DriveSync.isActive()) DriveSync.syncUpdatedSong((song))
        }
        transposeMinusButton.setOnClickListener { viewModel.transpose(false) }
        transposePlusButton.setOnClickListener { viewModel.transpose(true) }
    }

    private fun initStyles() {
        tabText.textSize = viewModel.getTextSize().toFloat()
        tabText.textColor = viewModel.getTextColor()
        tabParent.backgroundColor = viewModel.getBackgroundColor()
    }

    private fun initChords(chordData: ChordData) {
        chordsFlexLayout.removeAllViews()
        chordData.chords.forEach { chord ->
            val margin = DimensionUtils.dpToPx(2)
            val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(margin, margin, margin, margin)
            val chordText = LayoutInflater.from(this).inflate(R.layout.list_item_chord, null) as TextView
            chordText.layoutParams = layoutParams
            chordText.text = chord
            chordText.setOnClickListener {
                SoundPlayer.playChord(this, ChordHelper.toFlats(chord))
            }
            chordText.setOnLongClickListener {
                if (NetworkUtils.isInternetAvailable()) {
                    startActivity(Intent(this, ChordDataActivity::class.java).apply {
                        putExtra(Constants.EXTRA_CHORD, chord)
                    })
                } else {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.addToBackStack(null)
                    ChordDialogFragment(chord).show(transaction, "")
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