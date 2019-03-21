package com.evartem.invoiceman.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.evartem.invoiceman.navigation.MainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

//TODO: Layout = MOVE appBar to Fragment
abstract class AppBarFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).let {
            onConfigureBottomAppBar(it.getBottomAppBar(), it.getFloatingActionButton())
        }
    }

    protected abstract fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton)
}