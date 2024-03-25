package com.example.kotlininstagramapp.ui.Share

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import com.example.kotlininstagramapp.R
import com.squareup.picasso.Picasso

class ImageGridAdapter(private val context: Context, private val images: Array<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, images) {

}

