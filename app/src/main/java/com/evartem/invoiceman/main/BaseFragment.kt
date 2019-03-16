package com.evartem.invoiceman.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseFragment: Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).let {
            onConfigureBottomAppBar(it.getBottomAppBar(), it.getFloatingActionButton())
        }
    }
    protected abstract fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton)
}