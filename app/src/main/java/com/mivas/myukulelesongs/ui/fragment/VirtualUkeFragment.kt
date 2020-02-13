package com.mivas.myukulelesongs.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.mivas.myukulelesongs.R
import com.mivas.myukulelesongs.ui.adapter.VirtualUkeAdapter
import com.mivas.myukulelesongs.util.Constants
import com.mivas.myukulelesongs.util.Prefs
import kotlinx.android.synthetic.main.fragment_virtual_uke.*

/**
 * Fragment displaying a virtual uke.
 */
class VirtualUkeFragment : Fragment(R.layout.fragment_virtual_uke) {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var smoothScroller: LinearSmoothScroller
    private lateinit var virtualUkeAdapter: VirtualUkeAdapter
    private var scrollable = false
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == 0) scrollable = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
    }

    /**
     * Views initializer.
     */
    private fun initViews() {
        linearLayoutManager = object : LinearLayoutManager(activity!!, HORIZONTAL, false) {
            override fun canScrollHorizontally() = scrollable
        }
        smoothScroller = object : LinearSmoothScroller(activity!!) {
            override fun getHorizontalSnapPreference() = SNAP_TO_START
        }
        virtualUkeAdapter = VirtualUkeAdapter(activity!!)
        with (virtualUkeRecycler) {
            layoutManager = linearLayoutManager
            adapter = virtualUkeAdapter
            addOnScrollListener(scrollListener)
        }
    }

    /**
     * Listeners initializer.
     */
    private fun initListeners() {
        scrollBackwardButton.setOnClickListener {
            scrollable = true
            val nextPosition = if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() - 1 < 0) 0 else linearLayoutManager.findFirstCompletelyVisibleItemPosition() - 1
            smoothScroller.targetPosition = nextPosition
            linearLayoutManager.startSmoothScroll(smoothScroller)
        }
        scrollForwardButton.setOnClickListener {
            scrollable = true
            val nextPosition = linearLayoutManager.findFirstVisibleItemPosition()
            smoothScroller.targetPosition = nextPosition + 1
            linearLayoutManager.startSmoothScroll(smoothScroller)
        }
    }

}