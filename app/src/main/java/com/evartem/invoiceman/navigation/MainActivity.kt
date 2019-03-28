package com.evartem.invoiceman.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {

        var isProcessed = false

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is MviFragment<*, *, *> && fragment.onBackPressed())
            {
                isProcessed = true
                return@forEach
            }
        }

        if (!isProcessed) super.onBackPressed()
    }
}
