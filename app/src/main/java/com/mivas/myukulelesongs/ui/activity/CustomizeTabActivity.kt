package com.mivas.myukulelesongs.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProviders
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.viewmodel.CustomizeTabViewModel
import kotlinx.android.synthetic.main.activity_customize_tab.*
import org.jetbrains.anko.alert

class CustomizeTabActivity : AppCompatActivity() {

    private lateinit var viewModel: CustomizeTabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_tab)

        title = getString(R.string.customize_tab_activity_title)
        viewModel = ViewModelProviders.of(this).get(CustomizeTabViewModel::class.java)

        initViews()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_customize_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                showResetCustomizationsDialog()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        val textSize = viewModel.getTextSize()
        textSizeSeekBar.progress = textSize
        textSizeText.text = textSize.toString()
        textColorView.background = viewModel.createBackground(viewModel.getTextColor())
        chordColorView.background = viewModel.createBackground(viewModel.getChordColor())
        backgroundColorView.background = viewModel.createBackground(viewModel.getBackgroundColor())
    }

    private fun initListeners() {
        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.setTextSize(p0!!.progress)
                sendUpdateBroadcast()
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                textSizeText.text = p1.toString()
            }

        })
        textColorLayout.setOnClickListener {
            ColorPickerDialogBuilder
                .with(this)
                .initialColor(viewModel.getTextColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton(R.string.generic_ok) { _, color, _ ->
                    viewModel.setTextColor(color)
                    textColorView.background = viewModel.createBackground(color)
                    sendUpdateBroadcast()
                }
                .build()
                .show()
        }
        chordColorLayout.setOnClickListener {
            ColorPickerDialogBuilder
                .with(this)
                .initialColor(viewModel.getChordColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton(R.string.generic_ok) { _, color, _ ->
                    viewModel.setChordColor(color)
                    chordColorView.background = viewModel.createBackground(color)
                    sendUpdateBroadcast()
                }
                .build()
                .show()
        }
        backgroundColorLayout.setOnClickListener {
            ColorPickerDialogBuilder
                .with(this)
                .initialColor(viewModel.getBackgroundColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton(R.string.generic_ok) { _, color, _ ->
                    viewModel.setBackgroundColor(color)
                    backgroundColorView.background = viewModel.createBackground(color)
                    sendUpdateBroadcast()
                }
                .build()
                .show()
        }
    }

    private fun showResetCustomizationsDialog() {
        alert(R.string.customize_tab_activity_dialog_reset_description, R.string.customize_tab_activity_dialog_reset_title) {
            negativeButton(R.string.generic_cancel) {}
            positiveButton(R.string.generic_reset) {
                viewModel.reset()
                initViews()
                sendUpdateBroadcast()
            }
        }.show()
    }

    private fun sendUpdateBroadcast() = sendBroadcast(Intent(Constants.BROADCAST_CUSTOMIZATIONS_UPDATED))

}
