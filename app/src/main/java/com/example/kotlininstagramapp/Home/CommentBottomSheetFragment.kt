package com.example.kotlininstagramapp.Home

import Comment
import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentBottomSheetFragment(var postId: String) : BottomSheetDialogFragment() {

    var comments:ArrayList<Pair<Comment,Boolean>> = arrayListOf()
    lateinit var et_comment :EditText
    lateinit var tv_shareComment :TextView
    lateinit var recycleView :RecyclerView
    lateinit var shimmer: ShimmerFrameLayout
    lateinit var commentAdapter :CommentsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_comments,container,false)
        et_comment = view.findViewById(R.id.et_comment)
        tv_shareComment = view.findViewById(R.id.tv_sharecomment)
        shimmer = view.findViewById(R.id.shimmerLayout)
        val listViewShimmer = view.findViewById<ListView>(R.id.listViewShimmer)
        val items = Array(5) { "Item $it" }
        val adapter = SchimmerAdapter(requireContext(),  items)
        listViewShimmer.adapter = adapter


        CoroutineScope(Dispatchers.Main).launch{
            shimmer.startShimmer()
            withContext(Dispatchers.IO){
                comments = FirebaseHelper().getComments(postId)
            }
            shimmer.stopShimmer()
            shimmer.visibility =View.GONE
            recycleView = view.findViewById(R.id.rv_comments)
            commentAdapter = CommentsAdapter(view.context,comments)
            recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            recycleView.adapter = commentAdapter


        }

        tv_shareComment.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val text = et_comment.text.toString()
                    et_comment.text.clear()
                    withContext(Dispatchers.IO){
                        FirebaseHelper().publishComment(text,postId,commentAdapter)
                        Log.e("------------>","*** Yorum gönderme başarılı  ***")
                    }

                }catch (e:Throwable){
                    Log.e("------------>","Yorum gönderilirken bir hata oluştu : ${e.message}")
                }

            }
        }
        return view;
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetTransparent
    }


}