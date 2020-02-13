package com.mivas.myukulelesongs.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.util.SoundPlayer
import kotlinx.android.synthetic.main.list_item_virtual_uke.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.sdk25.coroutines.onTouch

/**
 * Adapter used to create the virtual uke in [com.mivas.myukulelesongs.ui.fragment.VirtualUkeFragment].
 */
class VirtualUkeAdapter(private val context: Context) : RecyclerView.Adapter<VirtualUkeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_virtual_uke, parent, false))
    override fun getItemCount() = 19
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            numberText.text = position.toString()
            parent.backgroundDrawable = if (position == 0) context.getDrawable(R.drawable.background_mahogany) else null
            val strings = listOf(aString, eString, cString, gString)
            for (i in 0..3) {
                strings[i].setOnClickListener {
                    val note = SoundPlayer.getNoteFromStrings(i, position)
                    SoundPlayer.playSound(context, i, note)
                }
            }
        }
    }

    /**
     * Fret ViewHolder.
     */
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val numberText = view.numberText!!
        val aString = view.aString!!
        val eString = view.eString!!
        val cString = view.cString!!
        val gString = view.gString!!
        val parent = view
    }

}