package com.example.kotlininstagramapp.Home

import Comment
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentBottomSheetFragment(var postId: String) : BottomSheetDialogFragment() {

    var comments:List<Comment> = listOf()
    lateinit var et_comment :EditText
    lateinit var tv_shareComment :TextView
    lateinit var recycleView :RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_comments,container,false)
        et_comment = view.findViewById(R.id.et_comment)
        tv_shareComment = view.findViewById(R.id.tv_sharecomment)

        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.IO){
                comments = FirebaseHelper().getComments(postId)
            }
            recycleView = view.findViewById(R.id.rv_comments)
            var commentAdapter = CommentsAdapter(view.context,comments)
            recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            recycleView.adapter = commentAdapter


        }

        tv_shareComment.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    et_comment.text.clear()
                    withContext(Dispatchers.IO){
                        FirebaseHelper().publishComment(et_comment.text.toString(),postId)
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