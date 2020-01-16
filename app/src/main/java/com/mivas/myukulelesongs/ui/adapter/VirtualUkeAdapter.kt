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
import org.jetbrains.anko.sdk25.coroutines.onTouch

class VirtualUkeAdapter(private val context: Context) : RecyclerView.Adapter<VirtualUkeAdapter.ViewHolder>() {

    private var hold = mutableListOf(-1, -1, -1, -1)
    private var lastPressed = mutableListOf(-1, -1, -1, -1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_virtual_uke, parent, false))
    override fun getItemCount() = 18
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            numberText.text = (position + 1).toString()
            val strings = listOf(aString, eString, cString, gString)
            val wires = listOf(aWire, eWire, cWire, gWire)
            for (i in 0..3) {
                strings[i].setOnTouchListener { _, event ->
                    when (event.action) {
                        ACTION_DOWN -> {
                            wires[i].setBackgroundColor(ContextCompat.getColor(context, R.color.gold))
                            if (lastPressed[i] >= 0 && hold[i] < 0) { // hold + press another --> register hold
                                hold[i] = lastPressed[i]
                                lastPressed[i] = position
                            } else { // store last pressed
                                lastPressed[i] = position
                            }
                            true
                        }
                        ACTION_UP -> {
                            wires[i].setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                            if (position == lastPressed[i] && hold[i] < 0) { // simple sound
                                lastPressed[i] = -1
                                //play sound simple
                                val note = SoundPlayer.getNoteFromStrings(i, 0)
                                SoundPlayer.playSound(context, i, note)
                            } else if (position == hold[i]) { // lift hold
                                lastPressed[i] = -1
                                hold[i] = -1
                            } else { // complex sound
                                lastPressed[i] = -1
                                val note = SoundPlayer.getNoteFromStrings(i, hold[i] + 1)
                                SoundPlayer.playSound(context, i, note)
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val numberText = view.numberText!!
        val aString = view.aString!!
        val eString = view.eString!!
        val cString = view.cString!!
        val gString = view.gString!!
        val aWire = view.aWire!!
        val eWire = view.eWire!!
        val cWire = view.cWire!!
        val gWire = view.gWire!!
        val parent = view
    }

}