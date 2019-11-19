package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.rest.model.ChordsXml
import com.mivas.myukulelesongs.ui.adapter.AlternativeAdapter
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.viewmodel.ChordViewModel
import com.mivas.myukulelesongs.viewmodel.factory.ChordViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chord.*

class ChordActivity : AppCompatActivity() {

    private lateinit var viewModel: ChordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chord)

        val viewModelFactory = ChordViewModelFactory(application, intent.getStringExtra(Constants.EXTRA_CHORD)!!)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ChordViewModel::class.java)

        initViews()
        initObservers()
    }

    private fun initViews() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "${intent.getStringExtra(Constants.EXTRA_CHORD)} Chord"
        }
        alternativeRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initObservers() {
        viewModel.getXmlData().observe(this, Observer<ChordsXml> {
            val diagramUrl = it.chords[0].diagram
            Picasso.get().load(diagramUrl).into(diagramImage)
            val photoUrl = it.chords[0].photo
            Picasso.get().load(photoUrl).into(photoImage)
            alternativeRecycler.adapter = AlternativeAdapter(this, viewModel.getAlternativeUrls())
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