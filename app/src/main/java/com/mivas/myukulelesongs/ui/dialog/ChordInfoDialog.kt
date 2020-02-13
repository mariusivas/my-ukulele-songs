package com.mivas.myukulelesongs.ui.dialog

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.mivas.myukulelesongs.R
import java.util.*

/**
 * Dialog for displaying chord info in case of no internet.
 */
class ChordInfoDialog(private val chord: String) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_chord_info, container, false)
        val image = view.findViewById(R.id.chordImage) as ImageView
        image.setImageDrawable(getDrawable(getChordFileName(chord)))
        return view
    }

    /**
     * Forms a filename form he given chord.
     *
     * @param chord The chord
     * @return The formed filename
     */
    private fun getChordFileName(chord: String): String {
        val fileChord = chord.toLowerCase(Locale.getDefault())
            .replace("a#", "bb")
            .replace("c#", "db")
            .replace("d#", "eb")
            .replace("f#", "gb")
            .replace("g#", "ab")
        return "chord_$fileChord"
    }

    /**
     * Searches the local project for a file matching the filename.
     *
     * @param filename The name of the file
     * @return The local resource file
     */
    private fun getDrawable(filename: String): Drawable {
        val resourceId = resources.getIdentifier(filename, "raw", context!!.packageName)
        return resources.getDrawable(resourceId)
    }
}