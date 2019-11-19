package com.mivas.myukulelesongs.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexboxLayout
import com.mivas.myukulelesongs.R
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
            scrollView.smoothScrollTo(0, scrollView.scrollY + 1)
            scrollHandler.postDelayed(this, 150 - scrollSeekBar.progress.toLong())
        }
    }
    private val customizationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            initStyles()
            tabText.setText(
                viewModel.getChordData(viewModel.getSong().value!!.tab).spannableBuilder,
                TextView.BufferType.SPANNABLE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        val viewModelFactory = TabViewModelFactory(application, intent.getLongExtra(EXTRA_ID, -1))
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TabViewModel::class.java)

        initStyles()
        initListeners()
        initObservers()

        registerReceiver(
            customizationsReceiver,
            IntentFilter(Constants.BROADCAST_CUSTOMIZATIONS_UPDATED)
        )
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
                val songsList = listOf(viewModel.getSong().value!!)
                val fileName = "${viewModel.getSong().value!!.title}.mus"
                ExportHelper.launchExportMusIntent(this, songsList, fileName)
                true
            }
            R.id.action_export_txt -> {
                val text = viewModel.getSong().value!!.tab
                val fileName = "${viewModel.getSong().value!!.title}.txt"
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

    private fun initObservers() {
        viewModel.getSong().observe(this, Observer<Song> {
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
                viewModel.getSong().value?.let { song ->
                    val chordData = viewModel.getChordData(song.tab)
                    keyText.text = KeyHelper.findKey(chordData.allChords)
                    initChords(chordData)
                    tabText.setText(chordData.spannableBuilder, TextView.BufferType.SPANNABLE)
                }
            }
        })
    }

    private fun initListeners() {
        scrollPlayButton.setOnClickListener {
            if (viewModel.scrollRunning) {
                stopScroll()
            } else {
                startScroll()
            }
        }
        scrollCloseButton.setOnClickListener { scrollTabView.visibility = View.GONE }
        transposeCloseButton.setOnClickListener {
            transposeView.visibility = View.GONE
            viewModel.transposedText.value = ""
            supportActionBar?.show()
        }
        transposeSaveButton.setOnClickListener {
            val song = viewModel.getSong().value!!.apply { tab = viewModel.transposedText.value!! }
            viewModel.updateSong(song)
            viewModel.transposedText.value = ""
            transposeView.visibility = View.GONE
            supportActionBar?.show()
        }
        transposeMinus.setOnClickListener { viewModel.transpose(false) }
        transposePlus.setOnClickListener { viewModel.transpose(true) }
    }

    private fun initStyles() {
        tabText.textSize = viewModel.getTextSize().toFloat()
        tabText.textColor = viewModel.getTextColor()
        tabParent.backgroundColor = viewModel.getBackgroundColor()
    }

    private fun initChords(chordData: ChordData) {
        chordsFlexLayout.removeAllViews()
        val inflater = LayoutInflater.from(this)
        chordData.chords.forEach { chord ->
            val margin = DimensionUtils.dpToPx(2)
            val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(margin, margin, margin, margin)
            val chordText = inflater.inflate(R.layout.list_item_chord, null) as TextView
            chordText.layoutParams = layoutParams
            chordText.text = chord
            chordText.setOnClickListener {
                if (NetworkUtils.isInternetAvailable()) {
                    startActivity(Intent(this, ChordActivity::class.java).apply {
                        putExtra(Constants.EXTRA_CHORD, chord)
                    })
                } else {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.addToBackStack(null)
                    ChordDialogFragment(chord).show(transaction, "")
                }
            }
            chordsFlexLayout.addView(chordText)
        }
    }

    private fun initOriginalKey() {
        val original = viewModel.getSong().value!!.originalKey
        originalKeyText.text = original
        originalKeyText.visibility = if (original.isEmpty()) View.GONE else View.VISIBLE
        originalKeyLabel.visibility = if (original.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun startScroll() {
        viewModel.scrollRunning = true
        scrollPlayButton.imageResource = R.drawable.selector_button_pause
        scrollHandler.postDelayed(scrollRunnable, 150 - scrollSeekBar.progress.toLong())
    }

    private fun stopScroll() {
        viewModel.scrollRunning = false
        scrollPlayButton.imageResource = R.drawable.selector_button_play
        scrollHandler.removeCallbacks(scrollRunnable)
    }

    override fun onDestroy() {
        unregisterReceiver(customizationsReceiver)
        super.onDestroy()
    }

}
