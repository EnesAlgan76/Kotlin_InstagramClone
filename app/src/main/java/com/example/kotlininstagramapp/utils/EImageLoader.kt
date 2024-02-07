package com.example.kotlininstagramapp.utils

import android.graphics.drawable.Drawable
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.example.kotlininstagramapp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class EImageLoader {

    companion object {

//        fun setImage(imgUrl: String, imageView: ImageView, mProgressBar: ProgressBar?) {
//            Picasso.get()
//                .load(imgUrl)
//                .error(R.drawable.icon_profile)
//                .into(imageView, object : Callback {
//                    override fun onSuccess() {
//                        if (mProgressBar != null) {
//                            mProgressBar.visibility = View.GONE
//                        }
//                    }
//
//                    override fun onError(e: Exception?) {
//                        if (mProgressBar != null) {
//                            mProgressBar.visibility = View.GONE
//                        }
//                    }
//                })
//        }

        fun setImage(imgUrl: String, imageView: ImageView, mProgressBar: ProgressBar?) {
            Glide.with(imageView.context)
                .load(imgUrl)
                .apply(RequestOptions.errorOf(R.drawable.icon_profile))
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        mProgressBar?.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        mProgressBar?.visibility = View.GONE
                        return false
                    }
                })
                .into(imageView)
        }




    }
}
