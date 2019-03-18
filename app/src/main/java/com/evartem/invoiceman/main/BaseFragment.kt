package com.evartem.invoiceman.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).let {
            onConfigureBottomAppBar(it.getBottomAppBar(), it.getFloatingActionButton())
        }
    }

    protected abstract fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton)

    protected fun randomGradientChart(
        peaksNumber: IntRange = 16..16, peaksHeight: IntRange = 5..45,
        startEndPeak: Float = 30f
    ): Array<Float> {
        val peaks = (1..peaksNumber.random()).map { peaksHeight.random().toFloat() }.toMutableList()
        peaks.add(0, startEndPeak)
        peaks.add(startEndPeak)
        return peaks.toTypedArray()
    }
}