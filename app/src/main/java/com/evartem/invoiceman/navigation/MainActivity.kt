package com.evartem.invoiceman.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.evartem.invoiceman.R
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomAppBar()

        fab.setOnClickListener { onFABClicked() }
    }

    private fun onFABClicked() {
        findNavController(R.id.navigation_host).popBackStack()
    }

    private fun setupBottomAppBar() {
        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }

    fun getBottomAppBar(): BottomAppBar = bottomAppBar

    fun getFloatingActionButton(): FloatingActionButton = fab
}
