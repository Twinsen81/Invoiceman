package com.evartem.invoiceman.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment

/**
 * This app is a single activity app. This [MainActivity] serves merely as a container for the
 * app's fragments that carry out all the functionality.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * Handle the hardware BACK button of the device.
     * Allow each child fragment to handle it. If they don't, then just let it work as intended, i.e.
     * go back the stack.
     */
    override fun onBackPressed() {

        var isProcessed = false

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is MviFragment<*, *, *> && fragment.onBackPressed()) {
                isProcessed = true
                return@forEach
            }
        }

        if (!isProcessed) super.onBackPressed()
    }
}
