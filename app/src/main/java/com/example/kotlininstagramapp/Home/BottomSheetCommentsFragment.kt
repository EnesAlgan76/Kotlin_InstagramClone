package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.kotlininstagramapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetCommentsFragment : BottomSheetDialogFragment() {

    private lateinit var listView: ListView

    // Sample data for the list
    private val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        listView = view.findViewById(R.id.listView)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            // Handle item click if needed
            val selectedItem = items[position]
            // Do something with the selected item
        }

        return view
    }
}
