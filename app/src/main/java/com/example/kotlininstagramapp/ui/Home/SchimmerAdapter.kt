package com.example.kotlininstagramapp.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.example.kotlininstagramapp.R

class SchimmerAdapter(context: Context, private val items: Array<String>) :
    ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cardView = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.shimmer_layout, parent, false) as LinearLayout
        } else {
            convertView as LinearLayout
        }


        return cardView
    }

    override fun getCount(): Int {
        return 6
    }
}