package com.example.kotlininstagramapp.utils

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.R
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class FiltersAdapter(
    private val filterList:  List<Pair<GPUImageFilter,String>>, // Now we pass GPUImageFilter objects
    private val originalImage: Bitmap, // We pass the original image
    private val filterClickListener: (GPUImageFilter) -> Unit
) : RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filters, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter  = filterList[position]
        holder.bind(filter, originalImage)
        holder.itemView.setOnClickListener {
            filterClickListener(filter.first)
        }
    }

    override fun getItemCount(): Int = filterList.size

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(filter: Pair<GPUImageFilter, String>, originalImage: Bitmap) {
            // Apply the filter to a thumbnail-sized copy of the original image
            val gpuImage = GPUImage(itemView.context)
            gpuImage.setImage(originalImage)
            gpuImage.setFilter(filter.first)

            // Set the filter preview image
            val filteredThumbnail = gpuImage.bitmapWithFilterApplied
            itemView.findViewById<ImageView>(R.id.iv_filter_thumbnail).setImageBitmap(filteredThumbnail)

            itemView.findViewById<TextView>(R.id.tv_filter_name).text = filter.second

        }
    }
}

