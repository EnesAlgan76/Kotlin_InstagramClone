package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import com.example.kotlininstagramapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetCommentsFragment : BottomSheetDialogFragment() {

    private lateinit var listView: ListView

    // Sample data for the list
    private val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_comments, container, false)

        val root = view.findViewById<View>(R.id.bottom_sheet_root)
       // listView = view.findViewById(R.id.listViewsheet)
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
//        listView.adapter = adapter
//
//        listView.setOnItemClickListener { _, _, position, _ ->
//            // Handle item click if needed
//            val selectedItem = items[position]
//            // Do something with the selected item
//        }

        return view
    }
}
