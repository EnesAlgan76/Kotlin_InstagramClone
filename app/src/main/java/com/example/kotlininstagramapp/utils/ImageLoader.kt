package com.example.kotlininstagramapp.utils

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.kotlininstagramapp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ImageLoader {

    companion object{

        fun setImage(imgUrl :String, imageView: ImageView, mProgressBar: ProgressBar){
            Picasso.get()
                .load(imgUrl)
                .error(R.drawable.icon_profile)
                .into(imageView, object: Callback{
                    override fun onSuccess() {
                        mProgressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        mProgressBar.visibility = View.GONE
                    }

                })
        }
    }
}