package com.example.kotlininstagramapp.Home

import Comment
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.TextHighlighter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentBottomSheetFragment(var postId: String, var userId: String, var userPostUrl: String) : BottomSheetDialogFragment()  {

    var comments:ArrayList<Pair<Comment,Boolean>> = arrayListOf()
    lateinit var et_comment :EditText
    lateinit var iv_comment_profile :ImageView
    lateinit var tv_shareComment :TextView
    lateinit var recycleView :RecyclerView
    lateinit var shimmer: ShimmerFrameLayout
    lateinit var commentAdapter :CommentsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_comments,container,false)
        et_comment = view.findViewById(R.id.et_comment)
        tv_shareComment = view.findViewById(R.id.tv_sharecomment)
        shimmer = view.findViewById(R.id.shimmerLayout)
        iv_comment_profile = view.findViewById(R.id.iv_comment_profile)

        val listViewShimmer = view.findViewById<ListView>(R.id.listViewShimmer)
        val items = Array(5) { "Item $it" }
        val adapter = SchimmerAdapter(requireContext(),  items)
        listViewShimmer.adapter = adapter

        Glide.with(requireContext()).load(UserSingleton.user!!.userDetails.profilePicture).into(iv_comment_profile)

       // setSpecialWordFormatting(et_comment)


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
                        FirebaseHelper().sendCommentNotification(userId, userPostUrl, text )
                        Log.e("------------>","*** Yorum gönderme başarılı  ***")
                    }

                }catch (e:Throwable){
                    Log.e("------------>","Yorum gönderilirken bir hata oluştu : ${e.message}")
                }

            }
        }
        return view;
    }




    override fun onDestroy() {
        super.onDestroy()
        Log.e("sdfs","FRAGMENT DESTROY EDİLDİ")
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetTransparent
    }


    fun setSpecialWordFormatting(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                et_comment.removeTextChangedListener(this) // Remove the TextWatcher temporarily

                val text = et_comment.text.toString()
                val highlightedText = TextHighlighter.highlightWords(text)
                val editableText = Editable.Factory.getInstance().newEditable(highlightedText)
                et_comment.text.replace(0, et_comment.length(), editableText, 0, editableText.length)

                et_comment.addTextChangedListener(this) // Reattach the TextWatcher
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }





}