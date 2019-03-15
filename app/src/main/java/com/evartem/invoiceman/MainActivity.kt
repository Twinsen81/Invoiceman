package com.evartem.invoiceman

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomAppBar()
    }

    private fun setupBottomAppBar() {
        //setSupportActionBar(bottomAppBar)
        bottomAppBar.replaceMenu(R.menu.main)
        bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    Toast.makeText(this, "Clicked menu item 1", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_home -> {
                    Toast.makeText(this, "Clicked menu item 2", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_notifications -> {
                    Toast.makeText(this, "Clicked menu item 3", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }
}
