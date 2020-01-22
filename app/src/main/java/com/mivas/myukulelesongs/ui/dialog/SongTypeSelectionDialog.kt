package com.mivas.myukulelesongs.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.ui.activity.AddEditSongActivity
import com.mivas.myukulelesongs.util.Constants

class SongTypeSelectionDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_song_type_selection, container, false)
        val chordsLayout = view.findViewById(R.id.chordsLayout) as View
        val tabLayout = view.findViewById(R.id.tabLayout) as View
        chordsLayout.setOnClickListener {
            startActivity(Intent(activity, AddEditSongActivity::class.java).putExtra(Constants.EXTRA_IS_TAB, false))
            dismiss()
        }
        tabLayout.setOnClickListener {
            startActivity(Intent(activity, AddEditSongActivity::class.java).putExtra(Constants.EXTRA_IS_TAB, true))
            dismiss()
        }
        return view
    }

}