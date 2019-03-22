package com.evartem.invoiceman.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.evartem.invoiceman.R
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.evartem.invoiceman.navigation.MainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_invoices.*

open class AppBarFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupBottomAppBarAndFAB()
    }

    private fun setupBottomAppBarAndFAB() {
        val bottomAppBar: BottomAppBar? = view?.findViewById(R.id.bottomAppBar)
        val fab: FloatingActionButton? = view?.findViewById(R.id.fab)
        if (bottomAppBar != null && fab != null) {
            setupBottomAppBarNavigation()
            onConfigureBottomAppBar(bottomAppBar, fab)
            fab.setOnClickListener { onFABClicked() }
        }
    }

    private fun setupBottomAppBarNavigation() {
        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }

    protected open fun onFABClicked() {
        Navigation.findNavController((activity as MainActivity).navigation_host.view!!).popBackStack()
    }

    protected open fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        fab.hide()
        bottomAppBar.visibility = View.GONE
    }
}