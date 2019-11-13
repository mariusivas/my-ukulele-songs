package com.mivas.myukulelesongs.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.rest.model.ChordsXml
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

        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_plus)

        val viewModelFactory = ChordViewModelFactory(application, intent.getStringExtra(Constants.EXTRA_CHORD)!!)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ChordViewModel::class.java)

        initListeners()
        initObservers()
    }

    private fun initListeners() {

    }

    private fun initObservers() {
            viewModel.getXmlData().observe(this, Observer<ChordsXml> {
                val diagramUrl = it.chords[0].miniDiagram
                Picasso.get().load(diagramUrl).into(diagramImage)
                val photoUrl = it.chords[0].photo
                Picasso.get().load(photoUrl).into(photoImage)
            })
    }

}