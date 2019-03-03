package com.evartem.invoiceman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    val inv = Invoice(
        UUID.randomUUID().toString(), 120, "23.09.19", "Siemens",
        listOf(
            Product(1, "6ES7-RTA-OAA0", "PLC", 2),
            Product(2, "6ES7-BBNF-OAA0", "Module", 1)
        ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        message.setOnClickListener {
            message.text = inv.products[1].article
        }
    }
}
