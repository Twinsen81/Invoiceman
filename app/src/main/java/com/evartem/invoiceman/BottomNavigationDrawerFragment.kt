package com.evartem.invoiceman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.nav_bottom_sheet.*

class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.nav_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation_view.setNavigationItemSelectedListener { menuItem ->
            // Bottom Navigation Drawer menu item clicks
            when (menuItem.itemId) {
                R.id.nav1 -> Toast.makeText(requireActivity(), "Clicked nav item 1", Toast.LENGTH_SHORT).show()
                R.id.nav2 -> Toast.makeText(requireActivity(), "Clicked nav item 2", Toast.LENGTH_SHORT).show()
                R.id.nav3 -> Toast.makeText(requireActivity(), "Clicked nav item 3", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
}