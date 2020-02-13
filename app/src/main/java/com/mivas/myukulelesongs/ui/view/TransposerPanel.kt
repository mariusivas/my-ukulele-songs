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
import com.mivas.myukulelesongs.listeners.TransposerListener
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import com.mivas.myukulelesongs.util.TransposeHelper
import kotlinx.android.synthetic.main.layout_tab_scroller_panel.view.*
import kotlinx.android.synthetic.main.layout_transposer_panel.view.*
import org.jetbrains.anko.imageResource

/**
 * Custom view that shows a panel which can transpose a tab.
 */
class TransposerPanel : ConstraintLayout {

    var listener: TransposerListener? = null
    var initialText: String? = null

    private var transposedText = ""

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_transposer_panel, this, true)
        initListeners()
    }

    /**
     * Listeners initializer.
     */
    private fun initListeners() {
        transposeCloseButton.setOnClickListener {
            listener?.onTextTransposed("", true)
            setVisible(false)
        }
        transposeSaveButton.setOnClickListener {
            listener?.onTextTransposed(transposedText, true)
            setVisible(false)
        }
        transposeMinusButton.setOnClickListener {
            transpose(false)
            listener?.onTextTransposed(transposedText)
        }
        transposePlusButton.setOnClickListener {
            transpose(true)
            listener?.onTextTransposed(transposedText)
        }
    }

    /**
     * Transposes the text.
     *
     * @param plus True if the text is transposed up, else false
     */
    private fun transpose(plus: Boolean) {
        val preferSharp = Prefs.getBoolean(Constants.PREF_PREFER_SHARP)
        transposedText = TransposeHelper.transposeSong(initialText!!, plus, preferSharp)
        initialText = transposedText
    }

    /**
     * Sets the view visibility.
     *
     * @param visible True if the panel should be visible, else false
     */
    fun setVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

}