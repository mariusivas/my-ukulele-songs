package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.ukulelechords.model.ChordsXml
import com.mivas.myukulelesongs.ui.adapter.AlternativeAdapter
import com.mivas.myukulelesongs.util.Constants.EXTRA_CHORD
import com.mivas.myukulelesongs.viewmodel.ChordInfoViewModel
import com.mivas.myukulelesongs.viewmodel.factory.ChordViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chord_data.*

/**
 * Activity showing data about a chord.
 */
class ChordInfoActivity : AppCompatActivity(R.layout.activity_chord_data) {

    private lateinit var viewModel: ChordInfoViewModel
    private lateinit var chord: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initExtras()
        initViewModel()
        initActionBar()
        initViews()
        initObservers()
    }

    /**
     * Extras initializer.
     */
    private fun initExtras() {
        chord = intent.getStringExtra(EXTRA_CHORD)!!
    }

    /**
     * ViewModel initializer.
     */
    private fun initViewModel() {
        val viewModelFactory = ChordViewModelFactory(chord)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ChordInfoViewModel::class.java)
    }

    /**
     * Views initializer.
     */
    private fun initViews() {
        alternativeRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * Action bar initializer.
     */
    private fun initActionBar() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "$chord Chord"
        }
    }

    /**
     * Observers initializer.
     */
    private fun initObservers() {
        viewModel.xmlData.observe(this, Observer<ChordsXml> {
            if (it.chords.isNotEmpty()) {
                val diagramUrl = it.chords[0].diagram
                val photoUrl = it.chords[0].photo
                Picasso.get().load(diagramUrl).into(diagramImage)
                Picasso.get().load(photoUrl).into(photoImage)
                alternativeRecycler.adapter = AlternativeAdapter(this, viewModel.getAlternativeUrls())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}