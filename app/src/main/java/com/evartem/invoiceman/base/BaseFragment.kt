package com.evartem.invoiceman.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.evartem.invoiceman.navigation.MainActivity
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
}