package com.mivas.myukulelesongs.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mivas.myukulelesongs.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_alternative.view.*

/**
 * Adapter for showing alternative chord datas in [com.mivas.myukulelesongs.ui.activity.ChordInfoActivity].
 */
class AlternativeAdapter(private val context: Context, private val urls: List<String>) : RecyclerView.Adapter<AlternativeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_alternative, parent, false))
    override fun getItemCount() = urls.size
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val url = urls[position]
        with(viewHolder) {
            Picasso.get().load(url).into(alternativeImage)
        }
    }

    /**
     * Alternative ViewHolder.
     */
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val alternativeImage = view.alternativeImage!!
    }

}