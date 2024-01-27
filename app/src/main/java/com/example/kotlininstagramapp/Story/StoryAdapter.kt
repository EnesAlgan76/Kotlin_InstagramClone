package com.example.kotlininstagramapp.Story

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R

class StoryAdapter(private val context: Context, private var data: List<String>) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = data[position]
        Glide.with(context)
            .load(imageUrl)
            .into(holder.imageView)

        holder.handleClick()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(horizontalItemList: List<String>) {
        data = horizontalItemList

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun handleClick() {
            itemView.setOnClickListener{
                Toast.makeText(context, "Tıklandı: ${imageView.resources.toString()}", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
