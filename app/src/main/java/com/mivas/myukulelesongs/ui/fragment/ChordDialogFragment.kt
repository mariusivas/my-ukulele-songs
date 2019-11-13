package com.mivas.myukulelesongs.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.util.ChordHelper
import java.util.*

class ChordDialogFragment(private val chord: String) : DialogFragment() {

    private lateinit var image: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_chord_image, container, false)
        image = view.findViewById(R.id.chordImage)
        image.setImageDrawable(getDrawable(getChordFileName(chord)))
        return view
    }

    private fun getChordFileName(chord: String): String {
        val fileChord = chord.toLowerCase(Locale.getDefault())
            .replace("a#", "bb")
            .replace("c#", "db")
            .replace("d#", "eb")
            .replace("f#", "gb")
            .replace("g#", "ab")
        return "chord_$fileChord"
    }

    private fun getDrawable(filename: String): Drawable {
        val resourceId = resources.getIdentifier(filename, "raw", context!!.packageName)
        return resources.getDrawable(resourceId)
    }
}