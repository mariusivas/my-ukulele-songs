package com.mivas.myukulelesongs.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.flexbox.FlexboxLayout
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.listeners.KeyPickerListener
import com.mivas.myukulelesongs.util.DimensionUtils
import com.mivas.myukulelesongs.util.KeyHelper

class KeyPickerDialog(private val listener: KeyPickerListener) : DialogFragment() {

    private lateinit var keysFlexbox: FlexboxLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_key_picker, container, false)
        keysFlexbox = view.findViewById(R.id.keysFlexbox)
        initFlexbox()
        val closeButton = view.findViewById(R.id.clearButton) as Button
        closeButton.setOnClickListener {
            listener.onKeyClicked("")
            dismiss()
        }
        return view
    }

    private fun initFlexbox() {
        val inflater = LayoutInflater.from(activity)
        KeyHelper.getAllKeys().forEach { key ->
            val margin = DimensionUtils.dpToPx(4)
            val layoutParams = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(margin, margin, margin, margin)
            val keyText = inflater.inflate(R.layout.list_item_key, null) as TextView
            keyText.layoutParams = layoutParams
            keyText.text = key
            keyText.setOnClickListener {
                listener.onKeyClicked(key)
                dismiss()
            }
            keysFlexbox.addView(keyText)
        }
    }

}