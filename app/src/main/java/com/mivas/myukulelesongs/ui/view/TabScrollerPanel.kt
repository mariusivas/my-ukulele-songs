package com.mivas.myukulelesongs.ui.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import kotlinx.android.synthetic.main.layout_tab_scroller_panel.view.*
import org.jetbrains.anko.imageResource

/**
 * Custom view that shows a panel which can auto scroll a ScrollView.
 */
class TabScrollerPanel : ConstraintLayout {

    var scrollView: ScrollView? = null // don't forget to attach the scrollView to this custom view

    private var scrollRunning = false
    private val scrollHandler = Handler()
    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (scrollSeekBar.progress != 0) {
                scrollView?.run { smoothScrollTo(0, scrollY + 1) }
            }
            scrollHandler.postDelayed(this, 105 - scrollSeekBar.progress.toLong())
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_tab_scroller_panel, this, true)
        initViews()
        initListeners()
    }

    /**
     * Views initializer.
     */
    private fun initViews() {
        scrollSeekBar.progress = Prefs.getInt(Constants.PREF_LAST_SCROLL_SPEED, Constants.DEFAULT_SCROLL_SPEED)
        scrollSpeedText.text = scrollSeekBar.progress.toString()
        scrollSpeedTextContainer.layoutParams = (scrollSpeedTextContainer.layoutParams as LayoutParams).apply { horizontalBias = scrollSeekBar.progress.toFloat() / 100f }
    }

    /**
     * Listeners initializer.
     */
    private fun initListeners() {
        scrollPlayButton.setOnClickListener { if (scrollRunning) stopScroll() else startScroll() }
        scrollCloseButton.setOnClickListener { setVisible(false) }
        scrollSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Prefs.putInt(Constants.PREF_LAST_SCROLL_SPEED, seekBar.progress)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                scrollSpeedText.text = progress.toString()
                scrollSpeedTextContainer.layoutParams = (scrollSpeedTextContainer.layoutParams as LayoutParams).apply { horizontalBias = progress.toFloat() / 100f }
            }
        })
    }

    /**
     * Starts the auto scroll.
     */
    private fun startScroll() {
        scrollRunning = true
        scrollPlayIcon.imageResource = R.drawable.selector_button_pause
        scrollHandler.postDelayed(scrollRunnable, 150 - scrollSeekBar.progress.toLong())
    }

    /**
     * Stops the auto scroll.
     */
    private fun stopScroll() {
        scrollRunning = false
        scrollPlayIcon.imageResource = R.drawable.selector_button_play
        scrollHandler.removeCallbacks(scrollRunnable)
    }

    /**
     * Sets the view visibility.
     *
     * @param visible True if the panel should be visible, else false
     */
    fun setVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * Gets the view visibility.
     *
     * @return True if the view is visible, else false
     */
    fun getVisible() = visibility == View.VISIBLE

}