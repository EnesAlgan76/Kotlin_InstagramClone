package com.example.kotlininstagramapp.Home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.Comment
import com.example.kotlininstagramapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    var comments:ArrayList<Comment> = arrayListOf()



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_comments,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_comments)

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for(index in 1..20){
            comments.add(Comment("kdfjh","Bu yorum test amaçlıdırBu yorum test amaçlıdırBu yorum test amaçlıdırBu yorum test amaçlıdır","44",
                "efw","https://marketplace.canva.com/EAFXS8-cvyQ/1/0/1600w/canva-brown-and-light-brown%2C-circle-framed-instagram-profile-picture-2PE9qJLmPac.jpg",
                "2s","ensalgn"))
        }





        var recycleView = view.findViewById<RecyclerView>(R.id.rv_comments)

        var commentAdapter: CommentsAdapter = CommentsAdapter(view.context,comments)
        recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)

        recycleView.adapter = commentAdapter

    }

    override fun getTheme(): Int {
        return R.style.BottomSheetTransparent
    }


}